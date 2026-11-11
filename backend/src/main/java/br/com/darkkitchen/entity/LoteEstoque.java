package br.com.darkkitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "lotes_estoque")
@Getter @Setter
public class LoteEstoque {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingrediente_id")
    private Ingrediente ingrediente;
    @Column(precision = 14, scale = 3)
    private BigDecimal quantidadeInicial;
    @Column(precision = 14, scale = 3)
    private BigDecimal quantidadeAtual;
    @Column(precision = 14, scale = 4)
    private BigDecimal custoUnitario;
    private LocalDate validade;
    private Instant entradaEm = Instant.now();
}
