package br.com.darkkitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "itens_compra")
@Getter @Setter
public class ItemCompra {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id")
    private PedidoCompra compra;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingrediente_id")
    private Ingrediente ingrediente;
    @Column(precision = 14, scale = 3)
    private BigDecimal quantidade;
    @Column(precision = 14, scale = 4)
    private BigDecimal custoUnitario;
    /** Preenchida no recebimento. */
    private LocalDate validade;
}
