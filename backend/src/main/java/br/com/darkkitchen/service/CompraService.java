package br.com.darkkitchen.service;

import br.com.darkkitchen.dto.CompraDto;
import br.com.darkkitchen.entity.*;
import br.com.darkkitchen.exception.NaoEncontradoException;
import br.com.darkkitchen.exception.RegraNegocioException;
import br.com.darkkitchen.repository.FornecedorRepository;
import br.com.darkkitchen.repository.IngredienteRepository;
import br.com.darkkitchen.repository.PedidoCompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/** Pedidos de compra ao fornecedor. */
@Service
@RequiredArgsConstructor
public class CompraService {
    private final PedidoCompraRepository compraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final IngredienteRepository ingredienteRepository;
    private final EstoqueService estoqueService;

    @Transactional(readOnly = true)
    public List<CompraDto.Resposta> listar() {
        return compraRepository.listarTodas().stream().map(CompraDto.Resposta::de).toList();
    }

    @Transactional(readOnly = true)
    public CompraDto.Resposta buscar(Long id) {
        return CompraDto.Resposta.de(compraRepository.buscarComItens(id)
                .orElseThrow(() -> new NaoEncontradoException("Compra não encontrada")));
    }

    @Transactional
    public CompraDto.Resposta criar(CompraDto.Requisicao requisicao) {
        Fornecedor fornecedor = fornecedorRepository.findById(requisicao.fornecedorId())
                .filter(Fornecedor::isAtivo)
                .orElseThrow(() -> new NaoEncontradoException("Fornecedor não encontrado"));

        PedidoCompra compra = new PedidoCompra();
        compra.setFornecedor(fornecedor);
        for (CompraDto.ItemRequisicao itemRequisicao : requisicao.itens()) {
            Ingrediente ingrediente = ingredienteRepository.findById(itemRequisicao.ingredienteId())
                    .filter(Ingrediente::isAtivo)
                    .orElseThrow(() -> new NaoEncontradoException("Ingrediente não encontrado: " + itemRequisicao.ingredienteId()));
            ItemCompra item = new ItemCompra();
            item.setCompra(compra);
            item.setIngrediente(ingrediente);
            item.setQuantidade(itemRequisicao.quantidade());
            item.setCustoUnitario(itemRequisicao.custoUnitario());
            compra.getItens().add(item);
        }
        return CompraDto.Resposta.de(compraRepository.save(compra));
    }

    @Transactional
    public CompraDto.Resposta enviar(Long id) {
        PedidoCompra compra = buscarParaAtualizar(id);
        if (compra.getStatus() != StatusCompra.ABERTO) throw new RegraNegocioException("Só compras abertas podem ser enviadas");
        compra.setStatus(StatusCompra.ENVIADO);
        return CompraDto.Resposta.de(compra);
    }

    @Transactional
    public CompraDto.Resposta cancelar(Long id) {
        PedidoCompra compra = buscarParaAtualizar(id);
        validarCompraEmAndamento(compra);
        compra.setStatus(StatusCompra.CANCELADO);
        return CompraDto.Resposta.de(compra);
    }

    /** Recebe a mercadoria: cria os lotes, soma no estoque e atualiza o custo dos ingredientes. */
    @Transactional
    public CompraDto.Resposta receber(Long id, CompraDto.RecebimentoRequisicao requisicao) {
        PedidoCompra compra = buscarParaAtualizar(id);
        validarCompraEmAndamento(compra);

        Set<Long> idsDosItens = new HashSet<>();
        compra.getItens().forEach(i -> idsDosItens.add(i.getId()));
        Map<Long, CompraDto.ItemRecebimentoRequisicao> informados = new HashMap<>();
        if (requisicao != null && requisicao.itens() != null) {
            for (CompraDto.ItemRecebimentoRequisicao informado : requisicao.itens()) {
                if (!idsDosItens.contains(informado.itemId()))
                    throw new RegraNegocioException("Item não pertence a esta compra: " + informado.itemId());
                informados.put(informado.itemId(), informado);
            }
        }

        // ordenado por ingrediente para travar sempre na mesma ordem
        List<ItemCompra> itens = compra.getItens().stream()
                .sorted(Comparator.comparing((ItemCompra i) -> i.getIngrediente().getId())).toList();
        for (ItemCompra item : itens) {
            CompraDto.ItemRecebimentoRequisicao informado = informados.get(item.getId());
            BigDecimal quantidade = informado != null && informado.quantidadeRecebida() != null
                    ? informado.quantidadeRecebida() : item.getQuantidade();
            BigDecimal custo = informado != null && informado.custoUnitario() != null
                    ? informado.custoUnitario() : item.getCustoUnitario();
            LocalDate validade = informado != null ? informado.validade() : null;

            item.setQuantidade(quantidade);
            item.setCustoUnitario(custo);
            item.setValidade(validade);
            estoqueService.registrarEntrada(item.getIngrediente().getId(), quantidade, custo, validade);
        }
        compra.setStatus(StatusCompra.RECEBIDO);
        compra.setRecebidoEm(Instant.now());
        return CompraDto.Resposta.de(compra);
    }

    private PedidoCompra buscarParaAtualizar(Long id) {
        return compraRepository.buscarParaAtualizar(id)
                .orElseThrow(() -> new NaoEncontradoException("Compra não encontrada"));
    }

    private void validarCompraEmAndamento(PedidoCompra compra) {
        if (compra.getStatus() == StatusCompra.RECEBIDO || compra.getStatus() == StatusCompra.CANCELADO)
            throw new RegraNegocioException("Esta compra já está " + compra.getStatus());
    }
}
