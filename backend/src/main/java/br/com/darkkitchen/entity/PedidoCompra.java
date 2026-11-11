package br.com.darkkitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos_compra")
@Getter @Setter
public class PedidoCompra {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;
    @Enumerated(EnumType.STRING)
    private StatusCompra status = StatusCompra.ABERTO;
    private Instant criadoEm = Instant.now();
    private Instant recebidoEm;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCompra> itens = new ArrayList<>();
}
