package com.automotiva.estetica.rick.api_agendamento_servicos.infra;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class EmailConfig {

    @Value("${spring.mail.username}")
    private String emailOrigem;

    @Value("${app.email.from.name:Estética Automotiva Rick}")
    private String nomeOrigem;
}
