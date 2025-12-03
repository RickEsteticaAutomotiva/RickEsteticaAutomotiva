package com.automotiva.estetica.rick.api_agendamento_servicos.exception;

import org.springframework.http.HttpStatus;

public class RecursoJaExisteException extends ApiBaseException {

    public RecursoJaExisteException(String tipo, String mensagem, String detalhes, HttpStatus status) {
        super(tipo, mensagem, detalhes, status);
    }

    public static class Builder
            extends ApiBaseException.Builder<RecursoJaExisteException, RecursoJaExisteException.Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public RecursoJaExisteException build() {
            this.tipo = "RECURSO_JA_EXISTE_EXCEPTION";
            this.status = HttpStatus.CONFLICT;
            return new RecursoJaExisteException(tipo, mensagem, detalhes, status);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
