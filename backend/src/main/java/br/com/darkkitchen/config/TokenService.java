package br.com.darkkitchen.config;

import br.com.darkkitchen.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class TokenService {
    private final SecretKey chave;
    private final long expiracaoMinutos;

    public TokenService(@Value("${app.jwt.segredo}") String segredo,
                        @Value("${app.jwt.expiracao-minutos}") long expiracaoMinutos) {
        byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) throw new IllegalStateException("JWT_SEGREDO precisa ter pelo menos 32 caracteres");
        this.chave = Keys.hmacShaKeyFor(bytes);
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public String gerar(Usuario usuario) {
        Date agora = new Date();
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("perfil", usuario.getPerfil().name())
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + expiracaoMinutos * 60_000))
                .signWith(chave)
                .compact();
    }

    public Optional<Claims> validar(String token) {
        try {
            return Optional.of(Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
