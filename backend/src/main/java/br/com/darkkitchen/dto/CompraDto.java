package br.com.darkkitchen.dto;

import br.com.darkkitchen.entity.ItemCompra;
import br.com.darkkitchen.entity.PedidoCompra;
import br.com.darkkitchen.entity.StatusCompra;
import br.com.darkkitchen.entity.Unidade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface CompraDto {

    record ItemRequisicao(@NotNull Long ingredienteId,
                          @NotNull @Positive BigDecimal quantidade,
                          @NotNull @PositiveOrZero BigDecimal custoUnitario) {}

    record Requisicao(@NotNull Long fornecedorId, @NotEmpty @Valid List<ItemRequisicao> itens) {}

    record ItemResposta(Long id, Long ingredienteId, String ingrediente, Unidade unidade,
                        BigDecimal quantidade, BigDecimal custoUnitario, LocalDate validade) {
        public static ItemResposta de(ItemCompra i) {
            return new ItemResposta(i.getId(), i.getIngrediente().getId(), i.getIngrediente().getNome(),
                    i.getIngrediente().getUnidade(), i.getQuantidade(), i.getCustoUnitario(), i.getValidade());
        }
    }

    record Resposta(Long id, Long fornecedorId, String fornecedor, StatusCompra status, BigDecimal total,
                    Instant criadoEm, Instant recebidoEm, List<ItemResposta> itens) {
        public static Resposta de(PedidoCompra c) {
            BigDecimal total = c.getItens().stream()
                    .map(i -> i.getQuantidade().multiply(i.getCustoUnitario()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            return new Resposta(c.getId(), c.getFornecedor().getId(), c.getFornecedor().getNome(), c.getStatus(),
                    total, c.getCriadoEm(), c.getRecebidoEm(), c.getItens().stream().map(ItemResposta::de).toList());
        }
    }

    /** Campos opcionais: o que não for informado vale o que foi pedido. */
    record ItemRecebimentoRequisicao(@NotNull Long itemId,
                                     @Positive BigDecimal quantidadeRecebida,
                                     @PositiveOrZero BigDecimal custoUnitario,
                                     LocalDate validade) {}

    record RecebimentoRequisicao(@Valid List<ItemRecebimentoRequisicao> itens) {}
}
