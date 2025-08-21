package com.automotiva.estetica.rick.api_agendamento_servicos.infra;

import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected ResponseEntity<?> definirRetorno(Integer statusCode, Object dados, String mensagem) {
        switch (statusCode) {
            case 200:
                return ResponseEntity.ok(dados);
            case 201:
                return ResponseEntity.status(201).body(dados);
            case 204:
                return ResponseEntity.noContent().build();
            case 400:
                return ResponseEntity.badRequest().body(mensagem);
            case 404:
                return ResponseEntity.notFound().build();
            case 500:
                return ResponseEntity.status(500).body(mensagem);
            default:
                return ResponseEntity.ok().build();
        }
    }
}
