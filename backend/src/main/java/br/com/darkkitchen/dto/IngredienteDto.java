package br.com.darkkitchen.dto;

import br.com.darkkitchen.entity.Ingrediente;
import br.com.darkkitchen.entity.Unidade;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public interface IngredienteDto {

    record Requisicao(@NotBlank @Size(max = 255) String nome,
                      @NotNull Unidade unidade,
                      @NotNull @PositiveOrZero BigDecimal estoqueMinimo,
                      @NotNull @PositiveOrZero BigDecimal custoUnitario) {}

    record Resposta(Long id, String nome, Unidade unidade, BigDecimal estoqueAtual,
                    BigDecimal estoqueMinimo, BigDecimal custoUnitario, boolean estoqueBaixo) {
        public static Resposta de(Ingrediente i) {
            return new Resposta(i.getId(), i.getNome(), i.getUnidade(), i.getEstoqueAtual(), i.getEstoqueMinimo(),
                    i.getCustoUnitario(), i.getEstoqueAtual().compareTo(i.getEstoqueMinimo()) <= 0);
        }
    }
}
