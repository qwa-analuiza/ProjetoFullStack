package br.com.darkkitchen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filtros(HttpSecurity http, FiltroJwt filtroJwt) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // público
                .requestMatchers("/error", "/api/auth/**",
                        "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cardapio/**").permitAll()
                // cozinha, gerente e admin
                .requestMatchers("/api/painel/**").hasAnyRole("COZINHEIRO", "GERENTE", "ADMIN")
                // motoboy, gerente e admin
                .requestMatchers("/api/entregas/**").hasAnyRole("MOTOBOY", "GERENTE", "ADMIN")
                // gerente e admin
                .requestMatchers("/api/ingredientes/**", "/api/fornecedores/**", "/api/estoque/**",
                        "/api/compras/**", "/api/dashboard/**").hasAnyRole("GERENTE", "ADMIN")
                // só admin
                .requestMatchers("/api/pratos/**", "/api/usuarios/**").hasRole("ADMIN")
                // qualquer usuário logado
                .requestMatchers("/api/pedidos/**", "/api/eu").authenticated()
                .anyRequest().denyAll())
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder();
    }
}
