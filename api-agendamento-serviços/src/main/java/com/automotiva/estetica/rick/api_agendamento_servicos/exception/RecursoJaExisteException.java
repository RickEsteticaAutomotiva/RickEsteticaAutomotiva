package com.automotiva.estetica.rick.api_agendamento_servicos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class RecursoJaExisteException extends RuntimeException {
    private final String valor;

    public RecursoJaExisteException(String valor) {
        this.valor = valor;
    }

    @Override
    public String getMessage() {
        return "%s já cadastrado no sistema".formatted(valor);
    }
}
