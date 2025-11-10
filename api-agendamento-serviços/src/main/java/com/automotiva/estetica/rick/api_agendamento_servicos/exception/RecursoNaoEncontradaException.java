package com.automotiva.estetica.rick.api_agendamento_servicos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class RecursoNaoEncontradaException extends ApiBaseException{

    public RecursoNaoEncontradaException(String tipo, String mensagem, String detalhes, HttpStatus status) {
        super(tipo, mensagem, detalhes, status);
    }

    public static class Builder extends ApiBaseException.Builder<RecursoNaoEncontradaException, Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public RecursoNaoEncontradaException build() {
            this.tipo = "RECURSO_NAO_ENCONTRADO_EXCEPTION";
            this.status = HttpStatus.NOT_FOUND;
            return new RecursoNaoEncontradaException(tipo, mensagem, detalhes, status);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
