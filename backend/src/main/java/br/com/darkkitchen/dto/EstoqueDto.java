package br.com.darkkitchen.dto;

import br.com.darkkitchen.entity.LoteEstoque;
import br.com.darkkitchen.entity.Unidade;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface EstoqueDto {

    record EntradaRequisicao(@NotNull Long ingredienteId,
                             @NotNull @Positive BigDecimal quantidade,
                             @NotNull @PositiveOrZero BigDecimal custoUnitario,
                             LocalDate validade) {}

    record LoteResposta(Long id, Long ingredienteId, String ingrediente, Unidade unidade,
                        BigDecimal quantidadeAtual, BigDecimal custoUnitario, LocalDate validade, boolean vencido) {
        public static LoteResposta de(LoteEstoque l, LocalDate hoje) {
            return new LoteResposta(l.getId(), l.getIngrediente().getId(), l.getIngrediente().getNome(),
                    l.getIngrediente().getUnidade(), l.getQuantidadeAtual(), l.getCustoUnitario(), l.getValidade(),
                    l.getValidade() != null && l.getValidade().isBefore(hoje));
        }
    }

    record AlertasResposta(List<IngredienteDto.Resposta> estoqueBaixo, List<LoteResposta> lotesVencendoOuVencidos) {}
}
