package br.com.darkkitchen.dto;

import br.com.darkkitchen.entity.Perfil;
import br.com.darkkitchen.entity.Usuario;
import jakarta.validation.constraints.*;

public interface UsuarioDto {

    /** Criação de funcionário (GERENTE, COZINHEIRO, MOTOBOY ou outro ADMIN) pelo ADMIN. */
    record Requisicao(@NotBlank @Size(max = 120) String nome,
                      @NotBlank @Email @Size(max = 255) String email,
                      @NotBlank @Size(min = 6, max = 72) String senha,
                      @NotNull Perfil perfil) {}

    record Resposta(Long id, String nome, String email, String telefone, String endereco, Perfil perfil, boolean ativo) {
        public static Resposta de(Usuario u) {
            return new Resposta(u.getId(), u.getNome(), u.getEmail(), u.getTelefone(), u.getEndereco(), u.getPerfil(), u.isAtivo());
        }
    }
}
