package com.automotiva.estetica.rick.domain.exception;

import org.springframework.http.HttpStatus;

public class AgendamentoInvalidoException extends DomainException {
    public AgendamentoInvalidoException(String tipo, String mensagem, String detalhes, HttpStatus status) {
        super(tipo, mensagem, detalhes, status);
    }

    public static class Builder
            extends
                DomainException.Builder<AgendamentoInvalidoException, AgendamentoInvalidoException.Builder> {

        @Override
        protected AgendamentoInvalidoException.Builder self() {
            return this;
        }

        @Override
        public AgendamentoInvalidoException build() {
            this.tipo = "AGENDAMENTO_INVALIDO";
            this.status = HttpStatus.UNPROCESSABLE_ENTITY;
            return new AgendamentoInvalidoException(tipo, mensagem, detalhes, status);
        }
    }

    public static AgendamentoInvalidoException.Builder builder() {
        return new AgendamentoInvalidoException.Builder();
    }
}
