package com.automotiva.estetica.rick.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração global do SpringDoc OpenAPI 2.x.
 *
 * <p>
 * Define o esquema de segurança Bearer JWT que exibe o botão "Authorize 🔒" no
 * Swagger UI e aplica o token em todos os endpoints da spec.
 *
 * <p>
 * Referência:
 * https://springdoc.org/#how-do-i-add-authorization-header-in-requests
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Rick Estética Automotiva — API de Agendamento").version("1.0.0")
                        .description("API REST para gerenciamento de agendamentos de serviços automotivos. "
                                + "Faça login em **POST /pessoas/login** para obter o token JWT "
                                + "e clique em **Authorize 🔒** para autenticar as demais requisições.")
                        .contact(new Contact().name("Rick Estética Automotiva")
                                .email("esteticaautomotivarick@gmail.com")))
                // Aplica o requisito de segurança globalmente em todos os endpoints
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components().addSecuritySchemes(BEARER_AUTH,
                        new SecurityScheme().name(BEARER_AUTH).type(SecurityScheme.Type.HTTP).scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Informe o token JWT obtido no endpoint POST /pessoas/login. "
                                        + "Não é necessário incluir o prefixo 'Bearer '.")));
    }
}
