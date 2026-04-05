package com.automotiva.estetica.rick.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("Testes de DomainException.Builder")
class DomainExceptionBuilderTest {

    @Test
    @DisplayName("builder base deve aplicar tipo, mensagem, detalhes e status")
    void builderBase_deveAplicarCampos() {
        var ex = TestException.builder()
                .tipo("REGRA_NEGOCIO")
                .mensagem("falha")
                .detalhes("detalhes")
                .status(HttpStatus.BAD_REQUEST)
                .build();

        assertEquals("REGRA_NEGOCIO", ex.getTipo());
        assertEquals("falha", ex.getMensagem());
        assertEquals("detalhes", ex.getDetalhes());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    private static final class TestException extends DomainException {

        private TestException(String tipo, String mensagem, String detalhes, HttpStatus status) {
            super(tipo, mensagem, detalhes, status);
        }

        static TestBuilder builder() {
            return new TestBuilder();
        }

        private static final class TestBuilder extends Builder<TestException, TestBuilder> {

            @Override
            protected TestBuilder self() {
                return this;
            }

            @Override
            public TestException build() {
                return new TestException(tipo, mensagem, detalhes, status);
            }
        }
    }
}

