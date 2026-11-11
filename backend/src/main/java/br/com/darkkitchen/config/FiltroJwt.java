package br.com.darkkitchen.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.com.darkkitchen.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

/** Lê o token do cabeçalho "Authorization: Bearer ..." e identifica o usuário. */
@Component
@RequiredArgsConstructor
public class FiltroJwt extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest requisicao, HttpServletResponse resposta, FilterChain cadeia)
            throws ServletException, IOException {
        String cabecalho = requisicao.getHeader("Authorization");
        if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
            // confere no banco: usuário desativado perde o acesso na hora, e o perfil vale o que está gravado agora
            tokenService.validar(cabecalho.substring(7)).ifPresent(dados ->
                    usuarioRepository.buscarPorEmail(dados.getSubject()).filter(u -> u.isAtivo()).ifPresent(usuario -> {
                        var autenticacao = new UsernamePasswordAuthenticationToken(usuario.getEmail(), null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name())));
                        SecurityContextHolder.getContext().setAuthentication(autenticacao);
                    }));
        }
        cadeia.doFilter(requisicao, resposta);
    }
}
