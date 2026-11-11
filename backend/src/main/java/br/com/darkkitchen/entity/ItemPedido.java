package br.com.darkkitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
@Getter @Setter
public class ItemPedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prato_id")
    private Prato prato;
    private Integer quantidade;
    @Column(length = 300)
    private String observacao;
    /** Preço e custo congelados no momento do pedido. */
    @Column(precision = 12, scale = 2)
    private BigDecimal precoUnitario;
    @Column(precision = 12, scale = 2)
    private BigDecimal custoUnitario;
}
