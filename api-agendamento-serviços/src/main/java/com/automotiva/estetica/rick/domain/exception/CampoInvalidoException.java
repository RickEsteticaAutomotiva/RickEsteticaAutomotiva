package com.automotiva.estetica.rick.domain.exception;

import org.springframework.http.HttpStatus;

public class CampoInvalidoException extends DomainException {

    public CampoInvalidoException(
            String tipo, String mensagem, String detalhes, HttpStatus status) {
        super(tipo, mensagem, detalhes, status);
    }

    public static class Builder extends DomainException.Builder<CampoInvalidoException, Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public CampoInvalidoException build() {
            this.tipo = "CAMPO_INVALIDO";
            this.status = HttpStatus.BAD_REQUEST;
            return new CampoInvalidoException(tipo, mensagem, detalhes, status);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
