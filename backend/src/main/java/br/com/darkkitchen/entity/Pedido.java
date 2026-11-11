package br.com.darkkitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter @Setter
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;
    @Enumerated(EnumType.STRING)
    private StatusPedido status = StatusPedido.RECEBIDO;
    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;
    private boolean pagamentoAprovado;
    @Column(length = 30)
    private String codigoPagamento;
    @Column(length = 300)
    private String enderecoEntrega;
    @Column(length = 30)
    private String telefoneContato;
    /** Motoboy que retirou o pedido para entrega (vazio até alguém retirar). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entregador_id")
    private Usuario entregador;

    @Column(precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    private Instant criadoEm = Instant.now();
    private Instant atualizadoEm = Instant.now();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();
}
