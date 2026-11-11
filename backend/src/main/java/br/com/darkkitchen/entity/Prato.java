package br.com.darkkitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pratos")
@Getter @Setter
public class Prato {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @Column(length = 1000)
    private String descricao;
    @Column(length = 100)
    private String categoria;
    @Column(length = 500)
    private String imagemUrl;
    @Column(precision = 12, scale = 2)
    private BigDecimal preco;
    private boolean ativo = true;

    /** Ficha técnica: ingredientes e quantidades por porção. */
    @OneToMany(mappedBy = "prato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemFicha> itens = new ArrayList<>();

    public BigDecimal calcularCusto() {
        return itens.stream()
                .map(ItemFicha::calcularCusto)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
