package com.automotiva.estetica.rick.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração isolada do PasswordEncoder.
 *
 * <p>
 * Manter este bean em uma classe separada de SecurityConfig é obrigatório para
 * evitar dependência circular: PessoaService precisa de PasswordEncoder, e
 * SecurityConfig precisaria de UserDetailsService (PessoaService). Ao extrair o
 * bean para cá, o Spring consegue instanciar PasswordEncoderConfig →
 * PessoaService → SecurityConfig sem ciclos.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
