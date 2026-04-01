package com.automotiva.estetica.rick.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para ErroLogResponseRedactor.
 *
 * <p>
 * Valida que respostas de logs de erro consultáveis tem dados sensíveis
 * adequadamente mascarados (IPs, emails, payloads) conforme OWASP A09.
 */
@DisplayName("ErroLogResponseRedactor — Redação de logs em resposta")
class ErroLogResponseRedactorTest {

    @Test
    @DisplayName("Deve redacionar resposta com payload sensível")
    void testRedactResponseWithSensitivePayload() {
        ErroLogResponse response = ErroLogResponse.builder().id(1L).timestamp(LocalDateTime.now())
                .tipoExcecao("java.lang.NullPointerException").mensagem("Erro ao processar pedido")
                .payloadRequisicao("{\"email\":\"user@test.com\", \"senha\":\"secret123\"}").endpoint("/api/login")
                .metodoHttp("POST").queryParams(null).headersRequisicao("Content-Type: application/json")
                .usuarioEmail("admin@test.com").statusHttp(500).ambiente("prod").ipCliente("192.168.1.100")
                .userAgent("Mozilla/5.0").stackTrace(null).build();

        ErroLogResponse redacted = ErroLogResponseRedactor.redactResponse(response);

        assertNotNull(redacted);
        assertFalse(redacted.getPayloadRequisicao().contains("secret123"), "Senha não deve estar visível");
        assertTrue(redacted.getPayloadRequisicao().contains("***REDACTED***"), "Payload deve estar redated");
    }

    @Test
    @DisplayName("Deve mascarar email do usuário deixando apenas domínio")
    void testMaskEmailInResponse() {
        ErroLogResponse response = ErroLogResponse.builder().id(1L).timestamp(LocalDateTime.now())
                .tipoExcecao("java.lang.Exception").mensagem("Erro")
                .payloadRequisicao("{\"dado\":\"valor\"}").endpoint("/api/test").metodoHttp("GET")
                .queryParams(null).headersRequisicao(null).usuarioEmail("rodrigo@example.com").statusHttp(500)
                .ambiente("prod").ipCliente("10.0.0.1").userAgent("Chrome").stackTrace(null).build();

        ErroLogResponse redacted = ErroLogResponseRedactor.redactResponse(response);

        assertNotNull(redacted.getUsuarioEmail());
        assertTrue(redacted.getUsuarioEmail().contains("@example.com"), "Domínio do email deve estar visível");
        assertTrue(redacted.getUsuarioEmail().startsWith("r***"), "Email deve ser mascarado como r***@domain");
        assertFalse(redacted.getUsuarioEmail().contains("rodrigo"), "Primeira parte do email deve estar mascarada");
    }

    @Test
    @DisplayName("Deve mascarar IP deixando apenas primeira octet")
    void testMaskIpAddressInResponse() {
        ErroLogResponse response = ErroLogResponse.builder().id(1L).timestamp(LocalDateTime.now())
                .tipoExcecao("java.lang.Exception").mensagem("Erro").payloadRequisicao(null).endpoint("/api/test")
                .metodoHttp("GET").queryParams(null).headersRequisicao(null).usuarioEmail("user@test.com")
                .statusHttp(500).ambiente("prod").ipCliente("192.168.1.100").userAgent("Chrome").stackTrace(null)
                .build();

        ErroLogResponse redacted = ErroLogResponseRedactor.redactResponse(response);

        assertNotNull(redacted.getIpCliente());
        assertTrue(redacted.getIpCliente().startsWith("192.168"), "Primeira parte do IP deve ser preservada");
        assertTrue(redacted.getIpCliente().contains("*"), "IP deve ser parcialmente mascarado");
        assertFalse(redacted.getIpCliente().endsWith(".100"), "Últimas octets devem estar mascaradas");
    }

    @Test
    @DisplayName("Deve redacionar stack trace com dados sensíveis")
    void testRedactStackTraceInResponse() {
        String stackTrace = "java.lang.Exception\n" + "\tat com.example.Service.process(192.168.1.1:8080)\n"
                + "\tat com.example.Controller.handle(admin@company.com)";

        ErroLogResponse response = ErroLogResponse.builder().id(1L).timestamp(LocalDateTime.now())
                .tipoExcecao("java.lang.Exception").mensagem("Erro").payloadRequisicao(null).endpoint("/api/test")
                .metodoHttp("GET").queryParams(null).headersRequisicao(null).usuarioEmail("user@test.com")
                .statusHttp(500).ambiente("prod").ipCliente("10.0.0.1").userAgent("Chrome").stackTrace(stackTrace)
                .build();

        ErroLogResponse redacted = ErroLogResponseRedactor.redactResponse(response);

        assertNotNull(redacted.getStackTrace());
        assertFalse(redacted.getStackTrace().contains("192.168.1.1"), "IP não deve estar visível na stack trace");
        assertFalse(redacted.getStackTrace().contains("admin@company.com"), "Email não deve estar visível");
        assertTrue(redacted.getStackTrace().contains("com.example"), "Nome da classe deve ser preservado");
    }

    @Test
    @DisplayName("Deve redacionar query params sensíveis")
    void testRedactQueryParamsInResponse() {
        ErroLogResponse response = ErroLogResponse.builder().id(1L).timestamp(LocalDateTime.now())
                .tipoExcecao("java.lang.Exception").mensagem("Erro").payloadRequisicao(null).endpoint("/api/test")
                .metodoHttp("GET").queryParams("page=1&api_key=secret789&filter=name").headersRequisicao(null)
                .usuarioEmail("user@test.com").statusHttp(500).ambiente("prod").ipCliente("10.0.0.1")
                .userAgent("Chrome").stackTrace(null).build();

        ErroLogResponse redacted = ErroLogResponseRedactor.redactResponse(response);

        assertNotNull(redacted.getQueryParams());
        assertFalse(redacted.getQueryParams().contains("secret789"), "API key não deve estar visível");
        assertTrue(redacted.getQueryParams().contains("page=1"), "Parâmetro não-sensível deve ser preservado");
    }

    @Test
    @DisplayName("Deve preservar resposta nula")
    void testPreserveNullResponse() {
        ErroLogResponse redacted = ErroLogResponseRedactor.redactResponse(null);

        assertTrue(redacted == null, "Resposta nula deve permanecer nula");
    }

    @Test
    @DisplayName("Deve preservar campos não-sensíveis da resposta")
    void testPreserveNonSensitiveFields() {
        LocalDateTime timestamp = LocalDateTime.now();
        ErroLogResponse response = ErroLogResponse.builder().id(123L).timestamp(timestamp)
                .tipoExcecao("java.lang.NullPointerException").mensagem("Erro ao processar").endpoint("/api/orders")
                .metodoHttp("POST").statusHttp(500).ambiente("prod").build();

        ErroLogResponse redacted = ErroLogResponseRedactor.redactResponse(response);

        assertTrue(redacted.getId().equals(123L), "ID deve ser preservado");
        assertTrue(redacted.getTimestamp().equals(timestamp), "Timestamp deve ser preservado");
        assertTrue(redacted.getTipoExcecao().equals("java.lang.NullPointerException"),
                "Tipo de exceção deve ser preservado");
        assertTrue(redacted.getMensagem().equals("Erro ao processar"), "Mensagem deve ser preservada");
        assertTrue(redacted.getEndpoint().equals("/api/orders"), "Endpoint deve ser preservado");
        assertTrue(redacted.getMetodoHttp().equals("POST"), "Método HTTP deve ser preservado");
        assertTrue(redacted.getStatusHttp().equals(500), "Status HTTP deve ser preservado");
        assertTrue(redacted.getAmbiente().equals("prod"), "Ambiente deve ser preservado");
    }

    @Test
    @DisplayName("Deve redacionar resposta completa com múltiplos dados sensíveis")
    void testRedactComplexResponse() {
        ErroLogResponse response = ErroLogResponse.builder().id(1L).timestamp(LocalDateTime.now())
                .tipoExcecao("com.example.ValidationException")
                .mensagem("Falha na validação de cadastro")
                .payloadRequisicao("{\"nome\":\"João\",\"cpf\":\"12345678901\",\"senha\":\"secret123\"}")
                .queryParams("token=eyJhbGciOiJIUzI1NiJ9.xyz&page=1")
                .stackTrace("Exception at Process(192.168.1.50:8080) by admin@company.com")
                .endpoint("/api/pessoas").metodoHttp("POST").headersRequisicao("Content-Type: application/json")
                .usuarioEmail("gerente@rick.com.br").statusHttp(422).ambiente("prod")
                .ipCliente("172.16.0.50").userAgent("RestClient/2.0").build();

        ErroLogResponse redacted = ErroLogResponseRedactor.redactResponse(response);

        // Validar redaction do payload
        assertFalse(redacted.getPayloadRequisicao().contains("12345678901"), "CPF não deve estar visível");
        assertFalse(redacted.getPayloadRequisicao().contains("secret123"), "Senha não deve estar visível");

        // Validar redaction de query params
        assertFalse(redacted.getQueryParams().contains("eyJhbGciOiJIUzI1NiJ9"), "Token não deve estar visível");

        // Validar redaction de stack trace
        assertFalse(redacted.getStackTrace().contains("192.168.1.50"), "IP não deve estar visível na stack");
        assertFalse(redacted.getStackTrace().contains("admin@company.com"), "Email não deve estar visível");

        // Validar mascaramento de email
        assertTrue(redacted.getUsuarioEmail().contains("@rick.com.br"), "Domínio deve ser preservado");

        // Validar mascaramento de IP
        assertTrue(redacted.getIpCliente().startsWith("172.16"), "Primeira octet deve ser preservada");

        // Campos não-sensíveis devem ser preservados
        assertTrue(redacted.getId().equals(1L), "ID deve ser preservado");
        assertTrue(redacted.getEndpoint().equals("/api/pessoas"), "Endpoint deve ser preservado");
        assertTrue(redacted.getStatusHttp().equals(422), "Status HTTP deve ser preservado");
    }

    @Test
    @DisplayName("Deve ignorar campos nulos ao redacionar")
    void testRedactResponseWithNullFields() {
        ErroLogResponse response = ErroLogResponse.builder().id(1L).timestamp(LocalDateTime.now())
                .tipoExcecao("java.lang.Exception").mensagem("Erro").payloadRequisicao(null).endpoint("/api/test")
                .metodoHttp("GET").queryParams(null).headersRequisicao(null).usuarioEmail(null).statusHttp(500)
                .ambiente("prod").ipCliente(null).userAgent(null).stackTrace(null).build();

        ErroLogResponse redacted = ErroLogResponseRedactor.redactResponse(response);

        assertNotNull(redacted);
        // Campos nulos devem permanecer como null ou sem erro
        assertTrue(redacted.getId().equals(1L), "ID deve ser preservado");
    }
}


