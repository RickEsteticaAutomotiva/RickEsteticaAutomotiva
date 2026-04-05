package com.automotiva.estetica.rick.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("Testes de PasswordEncoderConfig")
class PasswordEncoderConfigTest {

    private final PasswordEncoderConfig passwordEncoderConfig = new PasswordEncoderConfig();

    @Test
    @DisplayName("deve criar password encoder funcional")
    void passwordEncoder_deveRetornarEncoderValido() {
        PasswordEncoder passwordEncoder = passwordEncoderConfig.passwordEncoder();
        String senhaOriginal = "minhaSenha@123";
        String senhaCriptografada = passwordEncoder.encode(senhaOriginal);

        assertNotEquals(senhaOriginal, senhaCriptografada);
        assertTrue(passwordEncoder.matches(senhaOriginal, senhaCriptografada));
    }
}

