package br.com.darkkitchen.service;

import br.com.darkkitchen.dto.EstoqueDto;
import br.com.darkkitchen.dto.IngredienteDto;
import br.com.darkkitchen.entity.*;
import br.com.darkkitchen.exception.NaoEncontradoException;
import br.com.darkkitchen.exception.RegraNegocioException;
import br.com.darkkitchen.repository.IngredienteRepository;
import br.com.darkkitchen.repository.LoteEstoqueRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class EstoqueService {
    public static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private final IngredienteRepository ingredienteRepository;
    private final LoteEstoqueRepository loteRepository;
    private final EntityManager entityManager;

    public static LocalDate hoje() {
        return LocalDate.now(FUSO);
    }

    /** Trava o ingrediente e recarrega seus dados, para dois pedidos não gastarem o mesmo estoque. */
    private Ingrediente travar(Long ingredienteId) {
        Ingrediente ingrediente = ingredienteRepository.buscarParaAtualizar(ingredienteId)
                .orElseThrow(() -> new NaoEncontradoException("Ingrediente não encontrado: " + ingredienteId));
        entityManager.refresh(ingrediente);
        return ingrediente;
    }

    /** Entrada de mercadoria: cria o lote, soma no estoque e atualiza o custo (média ponderada). */
    @Transactional
    public LoteEstoque registrarEntrada(Long ingredienteId, BigDecimal quantidade, BigDecimal custo, LocalDate validade) {
        if (validade != null && validade.isBefore(hoje()))
            throw new RegraNegocioException("A validade informada já venceu");

        Ingrediente ingrediente = travar(ingredienteId);
        BigDecimal estoqueAnterior = ingrediente.getEstoqueAtual();
        BigDecimal novoEstoque = estoqueAnterior.add(quantidade);
        BigDecimal novoCusto = estoqueAnterior.multiply(ingrediente.getCustoUnitario())
                .add(quantidade.multiply(custo))
                .divide(novoEstoque, 4, RoundingMode.HALF_UP);
        ingrediente.setEstoqueAtual(novoEstoque);
        ingrediente.setCustoUnitario(novoCusto);

        LoteEstoque lote = new LoteEstoque();
        lote.setIngrediente(ingrediente);
        lote.setQuantidadeInicial(quantidade);
        lote.setQuantidadeAtual(quantidade);
        lote.setCustoUnitario(custo);
        lote.setValidade(validade);
        return loteRepository.save(lote);
    }

    @Transactional
    public EstoqueDto.LoteResposta registrarEntradaAvulsa(EstoqueDto.EntradaRequisicao requisicao) {
        LoteEstoque lote = registrarEntrada(requisicao.ingredienteId(), requisicao.quantidade(),
                requisicao.custoUnitario(), requisicao.validade());
        return EstoqueDto.LoteResposta.de(lote, hoje());
    }

    /** Baixa automática ao confirmar o pedido: gasta primeiro o lote que vence antes. */
    @Transactional
    public void baixarEstoqueDoPedido(Pedido pedido) {
        // soma o que cada ingrediente precisa no pedido inteiro (ordenado por id para travar sempre na mesma ordem)
        Map<Long, BigDecimal> necessario = new TreeMap<>();
        for (ItemPedido itemPedido : pedido.getItens())
            for (ItemFicha itemFicha : itemPedido.getPrato().getItens())
                necessario.merge(itemFicha.getIngrediente().getId(),
                        itemFicha.getQuantidade().multiply(BigDecimal.valueOf(itemPedido.getQuantidade())),
                        BigDecimal::add);

        for (Map.Entry<Long, BigDecimal> linha : necessario.entrySet()) {
            Ingrediente ingrediente = travar(linha.getKey());
            BigDecimal faltando = linha.getValue();
            for (LoteEstoque lote : loteRepository.listarUtilizaveis(ingrediente.getId(), hoje())) {
                if (faltando.signum() == 0) break;
                BigDecimal usado = lote.getQuantidadeAtual().min(faltando);
                lote.setQuantidadeAtual(lote.getQuantidadeAtual().subtract(usado));
                faltando = faltando.subtract(usado);
            }
            if (faltando.signum() > 0)
                throw new RegraNegocioException("Estoque insuficiente de " + ingrediente.getNome()
                        + " (faltam " + faltando.stripTrailingZeros().toPlainString() + " " + ingrediente.getUnidade() + ")");
            ingrediente.setEstoqueAtual(ingrediente.getEstoqueAtual().subtract(linha.getValue()));
        }
    }

    @Transactional(readOnly = true)
    public EstoqueDto.AlertasResposta listarAlertas(int diasParaVencer) {
        LocalDate hoje = hoje();
        List<IngredienteDto.Resposta> estoqueBaixo = ingredienteRepository.listarComEstoqueBaixo()
                .stream().map(IngredienteDto.Resposta::de).toList();
        List<EstoqueDto.LoteResposta> lotes = loteRepository.listarComValidadeAte(hoje.plusDays(diasParaVencer))
                .stream().map(l -> EstoqueDto.LoteResposta.de(l, hoje)).toList();
        return new EstoqueDto.AlertasResposta(estoqueBaixo, lotes);
    }

    @Transactional(readOnly = true)
    public List<EstoqueDto.LoteResposta> listarLotes() {
        LocalDate hoje = hoje();
        return loteRepository.listarComSaldo().stream().map(l -> EstoqueDto.LoteResposta.de(l, hoje)).toList();
    }

    /** Descarta um lote vencido ou estragado: tira o saldo dele do estoque. */
    @Transactional
    public EstoqueDto.LoteResposta descartarLote(Long loteId) {
        LoteEstoque lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new NaoEncontradoException("Lote não encontrado"));
        Ingrediente ingrediente = travar(lote.getIngrediente().getId());
        ingrediente.setEstoqueAtual(ingrediente.getEstoqueAtual().subtract(lote.getQuantidadeAtual()).max(BigDecimal.ZERO));
        lote.setQuantidadeAtual(BigDecimal.ZERO);
        return EstoqueDto.LoteResposta.de(lote, hoje());
    }
}
