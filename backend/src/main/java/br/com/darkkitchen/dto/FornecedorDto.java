package br.com.darkkitchen.dto;

import br.com.darkkitchen.entity.Fornecedor;
import jakarta.validation.constraints.*;

public interface FornecedorDto {

    record Requisicao(@NotBlank @Size(max = 255) String nome, @Size(max = 20) String cnpj,
                      @Email @Size(max = 255) String email, @Size(max = 30) String telefone) {}

    record Resposta(Long id, String nome, String cnpj, String email, String telefone) {
        public static Resposta de(Fornecedor f) {
            return new Resposta(f.getId(), f.getNome(), f.getCnpj(), f.getEmail(), f.getTelefone());
        }
    }
}
