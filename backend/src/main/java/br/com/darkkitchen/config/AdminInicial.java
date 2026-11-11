package br.com.darkkitchen.config;

import br.com.darkkitchen.entity.Perfil;
import br.com.darkkitchen.entity.Usuario;
import br.com.darkkitchen.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Cria o ADMIN inicial no banco (se ainda não existir), com e-mail e senha vindos das variáveis de ambiente. */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInicial implements ApplicationRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorDeSenha;
    @Value("${app.admin.nome}") private String nome;
    @Value("${app.admin.email}") private String email;
    @Value("${app.admin.senha}") private String senha;

    @Override
    public void run(ApplicationArguments argumentos) {
        String emailAdmin = email.trim().toLowerCase();
        if (usuarioRepository.existsByEmail(emailAdmin)) return;
        Usuario admin = new Usuario();
        admin.setNome(nome);
        admin.setEmail(emailAdmin);
        admin.setSenhaHash(codificadorDeSenha.encode(senha));
        admin.setPerfil(Perfil.ADMIN);
        usuarioRepository.save(admin);
        log.info("ADMIN inicial criado: {}", emailAdmin);
    }
}
