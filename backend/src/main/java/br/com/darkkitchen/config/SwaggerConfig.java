package br.com.darkkitchen.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Habilita o botão "Authorize" no Swagger para colar o token JWT. */
@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI documentacao() {
        return new OpenAPI()
                .info(new Info().title("Dark Kitchen API").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("token"))
                .components(new Components().addSecuritySchemes("token",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
