package br.com.darkkitchen.dto;

import br.com.darkkitchen.entity.FormaPagamento;
import br.com.darkkitchen.entity.ItemPedido;
import br.com.darkkitchen.entity.Pedido;
import br.com.darkkitchen.entity.StatusPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface PedidoDto {

    record ItemRequisicao(@NotNull Long pratoId,
                          @NotNull @Min(1) @Max(50) Integer quantidade,
                          @Size(max = 300) String observacao) {}

    record Requisicao(@NotEmpty @Valid List<ItemRequisicao> itens,
                      @NotNull FormaPagamento formaPagamento,
                      @Size(max = 300) String enderecoEntrega,
                      @Size(max = 30) String telefoneContato) {}

    record StatusRequisicao(@NotNull StatusPedido status) {}

    record ItemResposta(Long pratoId, String prato, Integer quantidade, String observacao,
                        BigDecimal precoUnitario, BigDecimal subtotal) {
        public static ItemResposta de(ItemPedido i) {
            return new ItemResposta(i.getPrato().getId(), i.getPrato().getNome(), i.getQuantidade(), i.getObservacao(),
                    i.getPrecoUnitario(), i.getPrecoUnitario().multiply(BigDecimal.valueOf(i.getQuantidade())));
        }
    }

    record Resposta(Long id, StatusPedido status, FormaPagamento formaPagamento, boolean pagamentoAprovado,
                    String codigoPagamento, String cliente, String enderecoEntrega, String telefoneContato,
                    String entregador, BigDecimal total, Instant criadoEm, Instant atualizadoEm, List<ItemResposta> itens) {
        public static Resposta de(Pedido p) {
            return new Resposta(p.getId(), p.getStatus(), p.getFormaPagamento(), p.isPagamentoAprovado(),
                    // pedido sem cliente (ex.: inserido direto no banco) não pode derrubar a fila inteira
                    p.getCodigoPagamento(), p.getCliente() == null ? "Cliente não identificado" : p.getCliente().getNome(),
                    p.getEnderecoEntrega(), p.getTelefoneContato(),
                    p.getEntregador() == null ? null : p.getEntregador().getNome(), p.getTotal(), p.getCriadoEm(), p.getAtualizadoEm(),
                    p.getItens().stream().map(ItemResposta::de).toList());
        }
    }
}
