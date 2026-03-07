package com.automotiva.estetica.rick.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção de domínio para falhas em integrações externas (Google Calendar, e-mail, etc.). Retorna
 * HTTP 502 Bad Gateway, indicando que o servidor externo retornou resposta inválida.
 */
public class IntegracaoException extends DomainException {

    public IntegracaoException(String tipo, String mensagem, String detalhes, HttpStatus status) {
        super(tipo, mensagem, detalhes, status);
    }

    public static class Builder extends DomainException.Builder<IntegracaoException, Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public IntegracaoException build() {
            this.tipo = "FALHA_INTEGRACAO";
            this.status = HttpStatus.BAD_GATEWAY;
            return new IntegracaoException(tipo, mensagem, detalhes, status);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
