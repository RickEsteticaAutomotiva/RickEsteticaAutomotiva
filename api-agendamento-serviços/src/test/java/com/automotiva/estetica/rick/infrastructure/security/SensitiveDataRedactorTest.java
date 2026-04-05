package com.automotiva.estetica.rick.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Testes unitários para SensitiveDataRedactor.
 *
 * <p>
 * Valida que chaves sensíveis são adequadamente redadas em payloads JSON,
 * form-data, query strings e stack traces.
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
    @DisplayName("Deve redacionar telefone em JSON")
    void testRedactTelefoneInJson() {
        String payload = "{\"telefone\":\"11987654321\", \"nome\":\"João\"}";
        String redacted = SensitiveDataRedactor.redactPayload(payload);

        assertFalse(redacted.contains("11987654321"), "Telefone não deve estar visível");
        assertTrue(redacted.contains("***REDACTED***"), "Deve conter marcador de redação");
        assertTrue(redacted.contains("João"), "Nome deve ser preservado");
    }

    @Test
    @DisplayName("Deve redacionar data_nascimento em JSON")
    void testRedactDataNascimentoInJson() {
        String payload = "{\"data_nascimento\":\"1990-01-15\", \"nome\":\"Maria\"}";
        String redacted = SensitiveDataRedactor.redactPayload(payload);

        assertFalse(redacted.contains("1990-01-15"), "Data de nascimento não deve estar visível");
        assertTrue(redacted.contains("***REDACTED***"), "Deve conter marcador de redação");
        assertTrue(redacted.contains("Maria"), "Nome deve ser preservado");
    }

    @Test
    @DisplayName("Deve redacionar dados bancários em JSON")
    void testRedactBancariosInJson() {
        String payload = "{\"banco\":\"001\", \"conta\":\"123456\", \"nome\":\"João\"}";
        String redacted = SensitiveDataRedactor.redactPayload(payload);

        assertFalse(redacted.contains("001"), "Banco não deve estar visível");
        assertFalse(redacted.contains("123456"), "Conta não deve estar visível");
        assertTrue(redacted.contains("João"), "Nome deve ser preservado");
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
    @DisplayName("Deve redacionar parametro sensível quando query inicia com ?")
    void testRedactQueryStringComInterrogacaoInicial() {
        String queryString = "?refresh_token=abc123&page=2";
        String redacted = SensitiveDataRedactor.redactPayload(queryString);

        assertFalse(redacted.contains("abc123"));
        assertTrue(redacted.contains("refresh_token=***REDACTED***"));
    }

    @Test
    @DisplayName("Deve preservar payload vazio ou nulo")
    void testPreserveNullOrEmptyPayload() {
        assertNull(SensitiveDataRedactor.redactPayload(null));
        assertTrue(SensitiveDataRedactor.redactPayload("").isEmpty());
        assertEquals("   ", SensitiveDataRedactor.redactPayload("   "));
    }

    @Test
    @DisplayName("Helper toCamelCase deve converter snake_case e manter texto simples")
    void testToCamelCaseHelper() {
        String camel = ReflectionTestUtils.invokeMethod(SensitiveDataRedactor.class, "toCamelCase", "refresh_token");
        String unchanged = ReflectionTestUtils.invokeMethod(SensitiveDataRedactor.class, "toCamelCase", "token");

        assertEquals("refreshToken", camel);
        assertEquals("token", unchanged);
    }

    @Test
    @DisplayName("Deve redacionar header Authorization")
    void testRedactAuthorizationHeader() {
        String redacted = SensitiveDataRedactor.redactHeader("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9...");

        assertTrue(redacted.contains("***REDACTED***"), "Authorization deve ser redated");
    }

    @Test
    @DisplayName("Deve preservar header quando valor for nulo ou vazio")
    void testRedactHeaderQuandoValorNuloOuBlank() {
        assertNull(SensitiveDataRedactor.redactHeader("Authorization", null));
        assertTrue(SensitiveDataRedactor.redactHeader("Authorization", "   ").isBlank());
    }

    @Test
    @DisplayName("Deve redacionar header sensível com case-insensitive")
    void testRedactHeaderCaseInsensitive() {
        String redacted = SensitiveDataRedactor.redactHeader("ToKeN", "abc123");

        assertTrue(redacted.contains("***REDACTED***"));
    }

    @Test
    @DisplayName("Deve preservar headers não-sensíveis")
    void testPreserveNonSensitiveHeader() {
        String value = "application/json";
        String redacted = SensitiveDataRedactor.redactHeader("Content-Type", value);

        assertEquals(value, redacted, "Headers não-sensíveis devem ser preservados");
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
        String payload = "{" + "\"nome\":\"João Silva\", " + "\"email\":\"joao@test.com\", "
                + "\"senha\":\"secret123\", " + "\"data_nascimento\":\"1990-01-01\", " + "\"cpf\":\"12345678901\""
                + "}";

        String redacted = SensitiveDataRedactor.redactPayload(payload);

        assertTrue(redacted.contains("João Silva"), "Nome deve ser preservado");
        assertTrue(redacted.contains("joao@test.com"), "Email deve ser preservado");
        assertFalse(redacted.contains("secret123"), "Senha deve ser redated");
        assertFalse(redacted.contains("12345678901"), "CPF deve ser redated");
    }

    // =========================================================================
    // TESTES DE STACK TRACE
    // =========================================================================

    @Test
    @DisplayName("Deve redacionar IPv4 em stack trace")
    void testRedactIpv4InStackTrace() {
        String stackTrace = "at com.example.Controller(192.168.1.100:8080) "
                + "NullPointerException: User from 10.0.0.5 not found";
        String redacted = SensitiveDataRedactor.redactStackTrace(stackTrace);

        assertFalse(redacted.contains("192.168.1.100"), "IPv4 não deve estar visível");
        assertFalse(redacted.contains("10.0.0.5"), "IPv4 não deve estar visível");
        assertTrue(redacted.contains("***REDACTED_IP***"), "Deve conter marcador de IP redated");
    }

    @Test
    @DisplayName("Deve redacionar emails em stack trace")
    void testRedactEmailInStackTrace() {
        String stackTrace = "at com.example.EmailService.send(user@example.com) "
                + "InvalidEmailException: admin@company.com is invalid";
        String redacted = SensitiveDataRedactor.redactStackTrace(stackTrace);

        assertFalse(redacted.contains("user@example.com"), "Email não deve estar visível");
        assertFalse(redacted.contains("admin@company.com"), "Email não deve estar visível");
        assertTrue(redacted.contains("@***REDACTED***"), "Deve conter marcador de email redated");
    }

    @Test
    @DisplayName("Deve redacionar JWT em stack trace")
    void testRedactJwtInStackTrace() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U";
        String stackTrace = "Authorization failed with token " + jwt + " at com.example.JwtFilter.doFilter";
        String redacted = SensitiveDataRedactor.redactStackTrace(stackTrace);

        assertFalse(redacted.contains(jwt), "JWT não deve estar visível");
        assertTrue(redacted.contains("***JWT_REDACTED***"), "Deve conter marcador de JWT redated");
    }

    @Test
    @DisplayName("Deve redacionar caminho de arquivo Unix em stack trace")
    void testRedactUnixPathInStackTrace() {
        String stackTrace = "at com.example.FileProcessor.read(/home/rodriguez/api/config.json) "
                + "FileNotFoundException at /home/admin/logs/error.log";
        String redacted = SensitiveDataRedactor.redactStackTrace(stackTrace);

        assertFalse(redacted.contains("/home/rodriguez"), "Caminho Unix não deve estar visível");
        assertFalse(redacted.contains("/home/admin"), "Caminho Unix não deve estar visível");
        assertTrue(redacted.contains("/home/***REDACTED***"), "Deve conter caminho redated");
    }

    @Test
    @DisplayName("Deve redacionar caminho de arquivo Windows em stack trace")
    void testRedactWindowsPathInStackTrace() {
        // String com escapes duplos para representar C:\Users\...
        String stackTrace = "at com.example.FileProcessor.read(C:\\Users\\rodriguez\\config.json) "
                + "FileNotFoundException at C:\\Users\\admin\\logs\\error.log";
        String redacted = SensitiveDataRedactor.redactStackTrace(stackTrace);

        assertFalse(redacted.contains("rodriguez"), "Caminho Windows não deve estar visível");
        assertFalse(redacted.contains("admin"), "Caminho Windows não deve estar visível");
        assertTrue(redacted.contains("C:\\Users\\***REDACTED***"), "Deve conter caminho redated");
    }

    @Test
    @DisplayName("Deve redacionar dados sensíveis em stack trace")
    void testRedactSensitiveDataInStackTrace() {
        // Use JSON-like format para que o regex capture
        String stackTrace = "ValidationError: {\"senha\":\"secret123\"} não é válida "
                + "at com.example.PessoaValidator.validate({\"cpf\":\"12345678901\"})";
        String redacted = SensitiveDataRedactor.redactStackTrace(stackTrace);

        assertFalse(redacted.contains("secret123"), "Senha não deve estar visível");
        assertFalse(redacted.contains("12345678901"), "CPF não deve estar visível");
    }

    @Test
    @DisplayName("Deve preservar stack trace vazio ou nulo")
    void testPreserveNullOrEmptyStackTrace() {
        assertNull(SensitiveDataRedactor.redactStackTrace(null));
        assertTrue(SensitiveDataRedactor.redactStackTrace("").isEmpty());
    }

    @Test
    @DisplayName("Deve preservar informações de compilação e classe em stack trace")
    void testPreserveClassInfoInStackTrace() {
        String stackTrace = """
                java.lang.NullPointerException
                	at com.automotiva.estetica.rick.application.service.PessoaService.criar(PessoaService.java:45)
                	at java.base/java.lang.Thread.run(Thread.java:834)
                """;
        String redacted = SensitiveDataRedactor.redactStackTrace(stackTrace);

        assertTrue(redacted.contains("java.lang.NullPointerException"));
        assertTrue(redacted.contains("com.automotiva.estetica.rick.application.service.PessoaService"));
        assertTrue(redacted.contains("PessoaService.java:45"));
    }
}
