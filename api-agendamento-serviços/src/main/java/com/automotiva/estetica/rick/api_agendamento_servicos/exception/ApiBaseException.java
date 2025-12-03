package com.automotiva.estetica.rick.api_agendamento_servicos.exception;

import org.springframework.http.HttpStatus;

public class ApiBaseException extends RuntimeException {
    private final String tipo;
    private final String mensagem;
    private final String detalhes;
    private final HttpStatus status;

    public ApiBaseException(String tipo, String message, String detalhes, HttpStatus status) {
        this.tipo = tipo;
        this.mensagem = message;
        this.detalhes = detalhes;
        this.status = status;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public abstract static class Builder<T extends ApiBaseException, B extends Builder<T, B>> {
        protected String tipo;
        protected String mensagem;
        protected String detalhes;
        protected HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        public B tipo(String tipo) {
            this.tipo = tipo;
            return self();
        }

        public B mensagem(String mensagem) {
            this.mensagem = mensagem;
            return self();
        }

        public B detalhes(String detalhes) {
            this.detalhes = detalhes;
            return self();
        }

        public B status(HttpStatus status) {
            this.status = status;
            return self();
        }

        protected abstract B self();

        public abstract T build();
    }
}
