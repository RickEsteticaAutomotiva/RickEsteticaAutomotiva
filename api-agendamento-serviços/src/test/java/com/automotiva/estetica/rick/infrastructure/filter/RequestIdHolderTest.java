package com.automotiva.estetica.rick.infrastructure.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para RequestIdHolder.
 *
 * <p>Valida que o requestId é gerado, armazenado e limpo corretamente no MDC.
 */
@DisplayName("RequestIdHolder — Correlação de requisições")
class RequestIdHolderTest {

    @BeforeEach
    @AfterEach
    void cleanup() {
        RequestIdHolder.clear();
    }

    @Test
    @DisplayName("Deve gerar e armazenar requestId único")
    void testGenerateAndStoreRequestId() {
        String requestId = RequestIdHolder.generateAndStoreRequestId();

        assertNotNull(requestId, "RequestId não deve ser nulo");
        assertNotNull(RequestIdHolder.getRequestId(), "RequestId deve estar armazenado no MDC");
        assertEquals(requestId, RequestIdHolder.getRequestId(),
                "RequestId armazenado deve corresponder ao gerado");
    }

    @Test
    @DisplayName("Deve retornar null quando requestId não foi definido")
    void testGetRequestIdWhenNotDefined() {
        assertNull(RequestIdHolder.getRequestId(), "Deve retornar null quando requestId não foi definido");
    }

    @Test
    @DisplayName("Deve limpar requestId do MDC")
    void testClearRequestId() {
        RequestIdHolder.generateAndStoreRequestId();
        assertNotNull(RequestIdHolder.getRequestId(), "RequestId deve estar definido antes de limpar");

        RequestIdHolder.clear();
        assertNull(RequestIdHolder.getRequestId(), "RequestId deve ser nulo após limpeza");
    }

    @Test
    @DisplayName("Deve gerar requestIds únicos em chamadas sucessivas")
    void testUniqueRequestIds() {
        String id1 = RequestIdHolder.generateAndStoreRequestId();
        String id1Retrieved = RequestIdHolder.getRequestId();

        RequestIdHolder.clear();

        String id2 = RequestIdHolder.generateAndStoreRequestId();
        String id2Retrieved = RequestIdHolder.getRequestId();

        assertNotNull(id1, "Primeiro requestId não deve ser nulo");
        assertNotNull(id2, "Segundo requestId não deve ser nulo");
        assertFalse(id1.equals(id2), "RequestIds sucessivos devem ser diferentes (UUID aleatório)");
        assertEquals(id1, id1Retrieved, "RequestId recuperado deve corresponder ao gerado");
        assertEquals(id2, id2Retrieved, "Segundo requestId recuperado deve corresponder ao gerado");
    }
}


