package br.com.darkkitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "itens_ficha")
@Getter @Setter
public class ItemFicha {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prato_id")
    private Prato prato;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingrediente_id")
    private Ingrediente ingrediente;
    /** Na mesma unidade do ingrediente. */
    @Column(precision = 14, scale = 4)
    private BigDecimal quantidade;

    public BigDecimal calcularCusto() {
        return quantidade.multiply(ingrediente.getCustoUnitario()).setScale(2, RoundingMode.HALF_UP);
    }
}
