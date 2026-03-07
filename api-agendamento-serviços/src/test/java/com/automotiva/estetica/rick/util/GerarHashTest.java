package com.automotiva.estetica.rick.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Verifica que o BCryptPasswordEncoder funciona corretamente para as senhas usadas nos seeds de
 * integração. Os hashes são gerados dinamicamente pelo AbstractIntegrationTest, portanto não
 * precisam ser hardcoded no SQL.
 */
class GerarHashTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("BCrypt encode/matches deve funcionar para as senhas dos seeds de teste")
    void bcrypt_encodeEMatches_deveFuncionar() {
        // Verifica que encode + matches é consistente para as senhas de teste
        String hashAdmin = encoder.encode("rick@2024");
        assertTrue(encoder.matches("rick@2024", hashAdmin), "Hash admin deve bater");

        String hashUser = encoder.encode("senha123");
        assertTrue(encoder.matches("senha123", hashUser), "Hash user deve bater");
    }
}
