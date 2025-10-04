package com.automotiva.estetica.rick.api_agendamento_servicos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class RecursoNaoEncontradaException extends RuntimeException{
    private final String nomeDependencia;

    public RecursoNaoEncontradaException(String nomeDependencia) {
        this.nomeDependencia = nomeDependencia;
    }

    @Override
    public String getMessage() {
        return "%s não encontrado(a) no sistema".formatted(nomeDependencia);
    }
}
