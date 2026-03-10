package com.automotiva.estetica.rick.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuração central do Flyway.
 *
 * <p>Responsabilidades:
 *
 * <ul>
 *   <li>Aplica convenções obrigatórias de migração (encoding, validação, sem out-of-order).
 *   <li>Desabilita o comando {@code flyway:clean} em produção via {@code cleanDisabled=true}.
 *   <li>Habilitado apenas nos profiles que usam banco real (dev, homolog, prod). Profiles "test" e
 *       "integration-test" configuram Flyway diretamente via properties.
 * </ul>
 *
 * <p>Não é necessário declarar um Bean {@link Flyway} manualmente — o Spring Boot auto-configura
 * com base nas propriedades {@code spring.flyway.*}. Este bean apenas customiza a configuração.
 */
@Configuration
@Profile({"dev", "homolog", "prod"})
public class FlywayConfig {

    /**
     * Customizador do Flyway aplicado antes da execução das migrações.
     *
     * <ul>
     *   <li>{@code encoding}: garante UTF-8 em todos os scripts SQL.
     *   <li>{@code outOfOrder(false)}: rejeita scripts fora de ordem, mantendo histórico linear.
     *   <li>{@code validateOnMigrate(true)}: falha o boot se checksums dos scripts mudarem.
     *   <li>{@code cleanDisabled(true)}: impede {@code flyway:clean} — proteção extra para prod.
     *   <li>{@code loggers}: usa o logger SLF4J integrado ao Spring.
     * </ul>
     */
    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return (FluentConfiguration config) ->
                config.encoding("UTF-8")
                        .outOfOrder(false)
                        .validateOnMigrate(true)
                        .cleanDisabled(true)
                        .loggers("slf4j");
    }
}
