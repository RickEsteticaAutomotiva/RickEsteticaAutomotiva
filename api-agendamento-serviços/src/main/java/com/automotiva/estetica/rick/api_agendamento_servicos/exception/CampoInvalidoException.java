package com.automotiva.estetica.rick.api_agendamento_servicos.exception;

import org.springframework.http.HttpStatus;

public class CampoInvalidoException extends ApiBaseException {

    public CampoInvalidoException(String tipo, String mensagem, String detalhes, HttpStatus status) {
        super(tipo, mensagem, detalhes, status);
    }

    public static class Builder extends ApiBaseException.Builder<CampoInvalidoException, Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public CampoInvalidoException build() {
            this.tipo = "CAMPO_INVALIDO_EXCEPTION";
            this.status = HttpStatus.NOT_FOUND;
            return new CampoInvalidoException(tipo, mensagem, detalhes, status);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
