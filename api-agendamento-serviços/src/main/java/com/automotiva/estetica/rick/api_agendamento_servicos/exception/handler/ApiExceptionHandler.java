package com.automotiva.estetica.rick.api_agendamento_servicos.exception.handler;

import com.automotiva.estetica.rick.api_agendamento_servicos.exception.ApiBaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiBaseException.class)
    public ResponseEntity<Object> handleApiBaseException(ApiBaseException ex) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatus().value());
        body.put("tipo", ex.getTipo());
        body.put("mensagem", ex.getMensagem());
        body.put("detalhes", ex.getDetalhes());

        return new ResponseEntity<>(body, ex.getStatus());
    }
}

