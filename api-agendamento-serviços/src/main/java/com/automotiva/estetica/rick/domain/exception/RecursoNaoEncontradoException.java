package com.automotiva.estetica.rick.domain.exception;

import org.springframework.http.HttpStatus;

public class RecursoNaoEncontradoException extends DomainException {

    public RecursoNaoEncontradoException(
            String tipo, String mensagem, String detalhes, HttpStatus status) {
        super(tipo, mensagem, detalhes, status);
    }

    public static class Builder
            extends DomainException.Builder<RecursoNaoEncontradoException, Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public RecursoNaoEncontradoException build() {
            this.tipo = "RECURSO_NAO_ENCONTRADO";
            this.status = HttpStatus.NOT_FOUND;
            return new RecursoNaoEncontradoException(tipo, mensagem, detalhes, status);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
