package com.automotiva.estetica.rick.domain.exception;

import org.springframework.http.HttpStatus;

public class RecursoJaExisteException extends DomainException {

    public RecursoJaExisteException(
            String tipo, String mensagem, String detalhes, HttpStatus status) {
        super(tipo, mensagem, detalhes, status);
    }

    public static class Builder extends DomainException.Builder<RecursoJaExisteException, Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public RecursoJaExisteException build() {
            this.tipo = "RECURSO_JA_EXISTE";
            this.status = HttpStatus.CONFLICT;
            return new RecursoJaExisteException(tipo, mensagem, detalhes, status);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
