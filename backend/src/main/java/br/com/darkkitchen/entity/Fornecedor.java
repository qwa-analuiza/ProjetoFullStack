package br.com.darkkitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fornecedores")
@Getter @Setter
public class Fornecedor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @Column(length = 20)
    private String cnpj;
    private String email;
    @Column(length = 30)
    private String telefone;
    private boolean ativo = true;
}
