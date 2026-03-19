package com.automotiva.estetica.rick.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para SensitiveDataRedactor.
 *
 * <p>Valida que chaves sensíveis são adequadamente redadas em payloads JSON,
 * form-data e query strings.
 */
@DisplayName("SensitiveDataRedactor — Redação de dados sensíveis")
class SensitiveDataRedactorTest {

    @Test
    @DisplayName("Deve redacionar senha em JSON")
    void testRedactSenhaInJson() {
        String payload = "{\"email\":\"user@test.com\", \"senha\":\"abc123\"}";
        String redacted = SensitiveDataRedactor.redactPayload(payload);

        assertFalse(redacted.contains("abc123"), "Senha não deve estar visível no payload redated");
        assertTrue(redacted.contains("***REDACTED***"), "Deve conter marcador de redação");
        assertTrue(redacted.contains("user@test.com"), "Email deve ser preservado");
    }

    @Test
    @DisplayName("Deve redacionar token em JSON")
    void testRedactTokenInJson() {
        String payload = "{\"token\":\"eyJhbGciOiJIUzUxMiJ9...\", \"nome\":\"João\"}";
        String redacted = SensitiveDataRedactor.redactPayload(payload);

        assertFalse(redacted.contains("eyJhbGciOiJIUzUxMiJ9"), "Token não deve estar visível");
        assertTrue(redacted.contains("***REDACTED***"), "Deve conter marcador de redação");
        assertTrue(redacted.contains("João"), "Nome deve ser preservado");
    }

    @Test
    @DisplayName("Deve redacionar CPF em JSON (case-insensitive)")
    void testRedactCpfInJson() {
        String payload = "{\"CPF\":\"12345678901\", \"nome\":\"Maria\"}";
        String redacted = SensitiveDataRedactor.redactPayload(payload);

        assertFalse(redacted.contains("12345678901"), "CPF não deve estar visível");
        assertTrue(redacted.contains("***REDACTED***"), "Deve conter marcador de redação");
        assertTrue(redacted.contains("Maria"), "Nome deve ser preservado");
    }

    @Test
    @DisplayName("Deve redacionar dados em form-data")
    void testRedactFormData() {
        String payload = "email=user@test.com&senha=secret123&nome=João";
        String redacted = SensitiveDataRedactor.redactPayload(payload);

        assertFalse(redacted.contains("secret123"), "Senha não deve estar visível no form");
        assertTrue(redacted.contains("***REDACTED***"), "Deve conter marcador de redação");
        assertTrue(redacted.contains("user@test.com"), "Email deve ser preservado");
        assertTrue(redacted.contains("João"), "Nome deve ser preservado");
    }

    @Test
    @DisplayName("Deve redacionar parametros em query string")
    void testRedactQueryString() {
        String queryString = "filtro=teste&api_key=secret789&page=1";
        String redacted = SensitiveDataRedactor.redactPayload(queryString);

        assertFalse(redacted.contains("secret789"), "API key não deve estar visível");
        assertTrue(redacted.contains("***REDACTED***"), "Deve conter marcador de redação");
        assertTrue(redacted.contains("page=1"), "Parametros não-sensíveis devem ser preservados");
    }

    @Test
    @DisplayName("Deve preservar payload vazio ou nulo")
    void testPreserveNullOrEmptyPayload() {
        assertTrue(SensitiveDataRedactor.redactPayload(null) == null);
        assertTrue(SensitiveDataRedactor.redactPayload("").isEmpty());
    }

    @Test
    @DisplayName("Deve redacionar header Authorization")
    void testRedactAuthorizationHeader() {
        String redacted = SensitiveDataRedactor.redactHeader("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9...");

        assertTrue(redacted.contains("***REDACTED***"), "Authorization deve ser redated");
    }

    @Test
    @DisplayName("Deve preservar headers não-sensíveis")
    void testPreserveNonSensitiveHeader() {
        String value = "application/json";
        String redacted = SensitiveDataRedactor.redactHeader("Content-Type", value);

        assertTrue(redacted.equals(value), "Headers não-sensíveis devem ser preservados");
    }

    @Test
    @DisplayName("Deve redacionar múltiplas ocorrências de mesma chave")
    void testRedactMultipleOccurrences() {
        String payload = "{\"senha\":\"pwd1\", \"confirmar_senha\":\"pwd1\"}";
        String redacted = SensitiveDataRedactor.redactPayload(payload);

        int redactedCount = (redacted.length() - redacted.replace("***REDACTED***", "").length())
                / "***REDACTED***".length();
        assertTrue(redactedCount >= 2, "Deve redatar ambas as ocorrências de chaves sensíveis");
    }

    @Test
    @DisplayName("Deve redacionar com variações de nomenclatura (underscore vs hífen)")
    void testRedactNomenclatureVariations() {
        String payload = "{\"refresh_token\":\"abc123\", \"refreshToken\":\"def456\"}";
        String redacted = SensitiveDataRedactor.redactPayload(payload);

        assertFalse(redacted.contains("abc123"));
        assertFalse(redacted.contains("def456"));
    }

    @Test
    @DisplayName("Deve preservar dados não-sensíveis mesmo com payload complexo")
    void testPreserveNonSensitiveInComplexPayload() {
        String payload = "{"
                + "\"nome\":\"João Silva\", "
                + "\"email\":\"joao@test.com\", "
                + "\"senha\":\"secret123\", "
                + "\"data_nascimento\":\"1990-01-01\", "
                + "\"cpf\":\"12345678901\""
                + "}";

        String redacted = SensitiveDataRedactor.redactPayload(payload);

        assertTrue(redacted.contains("João Silva"), "Nome deve ser preservado");
        assertTrue(redacted.contains("joao@test.com"), "Email deve ser preservado");
        assertTrue(redacted.contains("1990-01-01"), "Data deve ser preservada");
        assertFalse(redacted.contains("secret123"), "Senha deve ser redated");
        assertFalse(redacted.contains("12345678901"), "CPF deve ser redated");
    }
}

