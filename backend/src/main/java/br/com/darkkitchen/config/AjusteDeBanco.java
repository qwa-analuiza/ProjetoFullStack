package br.com.darkkitchen.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Bancos criados antes do perfil MOTOBOY têm a coluna "perfil" como ENUM do MySQL, que não aceita valores novos
 * (o ddl-auto=update não altera colunas existentes). Aqui ela vira texto simples; se já for texto, nada muda.
 */
@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class AjusteDeBanco implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments argumentos) {
        try {
            jdbc.execute("ALTER TABLE usuarios MODIFY COLUMN perfil VARCHAR(20) NOT NULL");
            log.info("Coluna usuarios.perfil conferida (texto, aceita MOTOBOY)");
        } catch (Exception e) {
            log.warn("Não foi possível ajustar usuarios.perfil automaticamente: {}", e.getMessage());
        }
    }
}
