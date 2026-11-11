package br.com.darkkitchen.dto;

import jakarta.validation.constraints.*;

public interface AutenticacaoDto {

    record CadastroRequisicao(@NotBlank @Size(max = 120) String nome,
                              @NotBlank @Email @Size(max = 255) String email,
                              @NotBlank @Size(min = 6, max = 72) String senha,
                              @NotBlank @Size(max = 300) String endereco,
                              @NotBlank @Size(max = 30) String telefone) {}

    record LoginRequisicao(@NotBlank @Email String email, @NotBlank String senha) {}

    record Resposta(String token, UsuarioDto.Resposta usuario) {}
}
