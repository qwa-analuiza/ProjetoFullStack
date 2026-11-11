package br.com.darkkitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "ingredientes")
@Getter @Setter
public class Ingrediente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nome;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Unidade unidade;
    /** Soma dos lotes. */
    @Column(precision = 14, scale = 3)
    private BigDecimal estoqueAtual = BigDecimal.ZERO;
    @Column(precision = 14, scale = 3)
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;
    /** Custo médio por unidade, atualizado a cada entrada de mercadoria. */
    @Column(precision = 14, scale = 4)
    private BigDecimal custoUnitario = BigDecimal.ZERO;
    private boolean ativo = true;
}
