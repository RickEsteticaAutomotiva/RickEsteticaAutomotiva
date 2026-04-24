package com.automotiva.estetica.rick.domain.exception;

import org.springframework.http.HttpStatus;

public class DataInvalidaException extends DomainException {

    public DataInvalidaException(String tipo, String mensagem, String detalhes, HttpStatus status) {
        super(tipo, mensagem, detalhes, status);
    }

    public static class Builder extends DomainException.Builder<DataInvalidaException, DataInvalidaException.Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public DataInvalidaException build() {
            this.tipo = "DATA_INVALIDA";
            this.status = HttpStatus.BAD_REQUEST;
            return new DataInvalidaException(tipo, mensagem, detalhes, status);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
