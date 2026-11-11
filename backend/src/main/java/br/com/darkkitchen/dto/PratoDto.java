package br.com.darkkitchen.dto;

import br.com.darkkitchen.entity.ItemFicha;
import br.com.darkkitchen.entity.Prato;
import br.com.darkkitchen.entity.Unidade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public interface PratoDto {

    record ItemFichaRequisicao(@NotNull Long ingredienteId, @NotNull @Positive BigDecimal quantidade) {}

    record Requisicao(@NotBlank @Size(max = 255) String nome,
                      @Size(max = 1000) String descricao,
                      @Size(max = 100) String categoria,
                      @Size(max = 500) String imagemUrl,
                      @NotNull @Positive BigDecimal preco,
                      Boolean ativo,
                      @NotEmpty @Valid List<ItemFichaRequisicao> ficha) {}

    record ItemFichaResposta(Long ingredienteId, String ingrediente, Unidade unidade, BigDecimal quantidade,
                             BigDecimal custoUnitario, BigDecimal custoDoItem) {
        public static ItemFichaResposta de(ItemFicha f) {
            return new ItemFichaResposta(f.getIngrediente().getId(), f.getIngrediente().getNome(),
                    f.getIngrediente().getUnidade(), f.getQuantidade(), f.getIngrediente().getCustoUnitario(),
                    f.calcularCusto());
        }
    }

    /** Visão do ADMIN: com ficha técnica, custo e margem. */
    record Resposta(Long id, String nome, String descricao, String categoria, String imagemUrl, BigDecimal preco,
                    boolean ativo, BigDecimal custoTotal, BigDecimal margemBruta, BigDecimal margemPercentual,
                    List<ItemFichaResposta> ficha) {
        public static Resposta de(Prato p) {
            BigDecimal custo = p.calcularCusto();
            BigDecimal margem = p.getPreco().subtract(custo);
            BigDecimal percentual = margem.multiply(BigDecimal.valueOf(100))
                    .divide(p.getPreco(), 1, RoundingMode.HALF_UP);
            return new Resposta(p.getId(), p.getNome(), p.getDescricao(), p.getCategoria(), p.getImagemUrl(),
                    p.getPreco(), p.isAtivo(), custo, margem, percentual,
                    p.getItens().stream().map(ItemFichaResposta::de).toList());
        }
    }

    /** Visão pública do cardápio: sem custo nem ficha. */
    record CardapioResposta(Long id, String nome, String descricao, String categoria, String imagemUrl, BigDecimal preco) {
        public static CardapioResposta de(Prato p) {
            return new CardapioResposta(p.getId(), p.getNome(), p.getDescricao(), p.getCategoria(), p.getImagemUrl(), p.getPreco());
        }
    }
}
