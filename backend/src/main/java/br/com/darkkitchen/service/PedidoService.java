package br.com.darkkitchen.service;

import br.com.darkkitchen.dto.PedidoDto;
import br.com.darkkitchen.entity.*;
import br.com.darkkitchen.exception.NaoEncontradoException;
import br.com.darkkitchen.exception.RegraNegocioException;
import br.com.darkkitchen.repository.PedidoRepository;
import br.com.darkkitchen.repository.PratoRepository;
import br.com.darkkitchen.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static br.com.darkkitchen.entity.StatusPedido.*;

@Service
@RequiredArgsConstructor
public class PedidoService {
    /** Para cada status, quais são os próximos permitidos. */
    private static final Map<StatusPedido, Set<StatusPedido>> PROXIMOS_STATUS = Map.of(
            RECEBIDO, Set.of(CONFIRMADO, CANCELADO),
            CONFIRMADO, Set.of(EM_PREPARO),
            EM_PREPARO, Set.of(PRONTO),
            PRONTO, Set.of(SAIU_PARA_ENTREGA),
            SAIU_PARA_ENTREGA, Set.of(ENTREGUE));

    /** O cozinheiro só mexe nestes status; a entrega é do gerente/admin. */
    private static final Set<StatusPedido> STATUS_DO_COZINHEIRO = Set.of(CONFIRMADO, EM_PREPARO, PRONTO);

    /** Pedidos "pendentes" que aparecem na tela da cozinha. */
    private static final List<StatusPedido> STATUS_PENDENTES = List.of(RECEBIDO, CONFIRMADO, EM_PREPARO, PRONTO);

    private final PedidoRepository pedidoRepository;
    private final PratoRepository pratoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstoqueService estoqueService;

    @Transactional
    public PedidoDto.Resposta criar(String emailCliente, PedidoDto.Requisicao requisicao) {
        Usuario cliente = buscarUsuario(emailCliente);
        Set<Long> idsPratos = requisicao.itens().stream().map(PedidoDto.ItemRequisicao::pratoId).collect(Collectors.toSet());
        Map<Long, Prato> pratos = pratoRepository.buscarVariosComFicha(idsPratos).stream()
                .collect(Collectors.toMap(Prato::getId, Function.identity()));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFormaPagamento(requisicao.formaPagamento());
        pedido.setEnderecoEntrega(vazio(requisicao.enderecoEntrega()) ? cliente.getEndereco() : requisicao.enderecoEntrega().trim());
        pedido.setTelefoneContato(vazio(requisicao.telefoneContato()) ? cliente.getTelefone() : requisicao.telefoneContato().trim());
        if (vazio(pedido.getEnderecoEntrega())) throw new RegraNegocioException("Informe o endereço de entrega");

        BigDecimal total = BigDecimal.ZERO;
        for (PedidoDto.ItemRequisicao itemRequisicao : requisicao.itens()) {
            Prato prato = pratos.get(itemRequisicao.pratoId());
            if (prato == null || !prato.isAtivo())
                throw new RegraNegocioException("Prato indisponível: " + itemRequisicao.pratoId());

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setPrato(prato);
            item.setQuantidade(itemRequisicao.quantidade());
            item.setObservacao(itemRequisicao.observacao());
            item.setPrecoUnitario(prato.getPreco());
            item.setCustoUnitario(prato.calcularCusto());
            pedido.getItens().add(item);
            total = total.add(prato.getPreco().multiply(BigDecimal.valueOf(itemRequisicao.quantidade())));
        }
        pedido.setTotal(total);

        // pagamento simulado: sempre aprovado
        pedido.setPagamentoAprovado(true);
        pedido.setCodigoPagamento("SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return PedidoDto.Resposta.de(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoDto.Resposta> listarDoCliente(String emailCliente) {
        Usuario cliente = buscarUsuario(emailCliente);
        return pedidoRepository.listarDoCliente(cliente.getId()).stream().map(PedidoDto.Resposta::de).toList();
    }

    /** O cliente só vê os próprios pedidos; funcionários veem todos. */
    @Transactional(readOnly = true)
    public PedidoDto.Resposta buscar(Long id, String email, boolean funcionario) {
        Pedido pedido = pedidoRepository.buscarComItens(id).orElseThrow(this::pedidoNaoEncontrado);
        if (!funcionario && !pedido.getCliente().getEmail().equals(email)) throw pedidoNaoEncontrado();
        return PedidoDto.Resposta.de(pedido);
    }

    /** Fila da cozinha: mais antigos primeiro. Sem filtro, mostra os pendentes. O cozinheiro só enxerga os pendentes. */
    @Transactional(readOnly = true)
    public List<PedidoDto.Resposta> listarFila(StatusPedido filtro, boolean cozinheiro) {
        if (cozinheiro && filtro != null && !STATUS_PENDENTES.contains(filtro)) return List.of();
        List<StatusPedido> status = filtro != null ? List.of(filtro) : STATUS_PENDENTES;
        return pedidoRepository.listarPorStatus(status).stream().map(PedidoDto.Resposta::de).toList();
    }

    // ---- entregas (motoboy) ----

    /** Pedidos prontos esperando alguém retirar. */
    @Transactional(readOnly = true)
    public List<PedidoDto.Resposta> listarProntosParaRetirada() {
        return pedidoRepository.listarPorStatus(List.of(PRONTO)).stream().map(PedidoDto.Resposta::de).toList();
    }

    /** Entregas do motoboy: as que estão em rota e as concluídas hoje. */
    @Transactional(readOnly = true)
    public List<PedidoDto.Resposta> listarEntregasDoEntregador(String emailEntregador) {
        Usuario entregador = buscarUsuario(emailEntregador);
        Instant inicioDoDia = EstoqueService.hoje().atStartOfDay(EstoqueService.FUSO).toInstant();
        return pedidoRepository.listarDoEntregador(entregador.getId()).stream()
                .filter(p -> p.getStatus() == SAIU_PARA_ENTREGA
                        || (p.getStatus() == ENTREGUE && !p.getAtualizadoEm().isBefore(inicioDoDia)))
                .map(PedidoDto.Resposta::de).toList();
    }

    /** O motoboy pega um pedido PRONTO: ele vira o responsável e o status passa a SAIU_PARA_ENTREGA. */
    @Transactional
    public PedidoDto.Resposta retirarParaEntrega(Long id, String emailEntregador) {
        Usuario entregador = buscarUsuario(emailEntregador);
        Pedido pedido = pedidoRepository.buscarParaAtualizar(id).orElseThrow(this::pedidoNaoEncontrado);
        if (pedido.getStatus() != PRONTO)
            throw new RegraNegocioException("Este pedido não está mais disponível para retirada");
        pedido.setEntregador(entregador);
        return mudarStatus(pedido, SAIU_PARA_ENTREGA);
    }

    /** Só quem retirou o pedido conclui a entrega (gerente e admin podem concluir qualquer uma). */
    @Transactional
    public PedidoDto.Resposta concluirEntrega(Long id, String emailEntregador, boolean gestor) {
        Pedido pedido = pedidoRepository.buscarParaAtualizar(id).orElseThrow(this::pedidoNaoEncontrado);
        if (pedido.getStatus() != SAIU_PARA_ENTREGA)
            throw new RegraNegocioException("Este pedido não está em rota de entrega");
        boolean ehDono = pedido.getEntregador() != null && pedido.getEntregador().getEmail().equals(emailEntregador);
        if (!gestor && !ehDono) throw new RegraNegocioException("Esta entrega está com outro motoboy");
        return mudarStatus(pedido, ENTREGUE);
    }

    @Transactional
    public PedidoDto.Resposta cancelarPeloCliente(Long id, String emailCliente) {
        Pedido pedido = pedidoRepository.buscarParaAtualizar(id).orElseThrow(this::pedidoNaoEncontrado);
        if (!pedido.getCliente().getEmail().equals(emailCliente)) throw pedidoNaoEncontrado();
        return mudarStatus(pedido, CANCELADO);
    }

    @Transactional
    public PedidoDto.Resposta atualizarStatus(Long id, StatusPedido novoStatus, boolean cozinheiro) {
        if (cozinheiro && !STATUS_DO_COZINHEIRO.contains(novoStatus))
            throw new RegraNegocioException("O cozinheiro só pode usar CONFIRMADO, EM_PREPARO e PRONTO");
        Pedido pedido = pedidoRepository.buscarParaAtualizar(id).orElseThrow(this::pedidoNaoEncontrado);
        return mudarStatus(pedido, novoStatus);
    }

    private PedidoDto.Resposta mudarStatus(Pedido pedido, StatusPedido novoStatus) {
        if (!PROXIMOS_STATUS.getOrDefault(pedido.getStatus(), Set.of()).contains(novoStatus))
            throw new RegraNegocioException("Mudança inválida: " + pedido.getStatus() + " → " + novoStatus);
        // ao confirmar, baixa o estoque; se faltar ingrediente, dá erro e nada é alterado
        if (novoStatus == CONFIRMADO) estoqueService.baixarEstoqueDoPedido(pedido);
        pedido.setStatus(novoStatus);
        pedido.setAtualizadoEm(Instant.now());
        return PedidoDto.Resposta.de(pedido);
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.buscarPorEmail(email).orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado"));
    }

    private NaoEncontradoException pedidoNaoEncontrado() {
        return new NaoEncontradoException("Pedido não encontrado");
    }

    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }
}
