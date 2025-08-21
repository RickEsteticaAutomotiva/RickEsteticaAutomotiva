package com.automotiva.estetica.rick.api_agendamento_servicos.infra;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SwaggerConfig {
    @Bean
    @Profile("!prod")
    public OpenAPI minhaApiPersonalizada() {
        return new OpenAPI()
                .info(new Info()
                        .title("Api Agendamento de Serviços Rick")
                        .version("1.0")
                        .description("Esta API tem como objetivo gerenciar o agendamento de serviços da RICK Estética Automotiva."));
    }
}
