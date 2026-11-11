package br.com.darkkitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "usuarios")
@Getter @Setter
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String senhaHash;
    @Column(length = 30)
    private String telefone;
    @Column(length = 300)
    private String endereco;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private Perfil perfil;
    private boolean ativo = true;
    private Instant criadoEm = Instant.now();
}
