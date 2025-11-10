package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CalendarEventRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.CalendarioGoogleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calendario/eventos")
@RequiredArgsConstructor
public class CalendarioGoogleController {

    @Autowired
    private final CalendarioGoogleService calendarioService;

    @GetMapping("/saude")
    public ResponseEntity<?> verificarSaude() {
        boolean disponivel = calendarioService.estaDisponivel();
        if (disponivel) {
            return ResponseEntity.status(200).build();
        } else {
            return ResponseEntity.status(503).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> criarEvento(@RequestBody CalendarEventRequest request) {
        ResponseEntity<?> respostaIndisponivel = verificarDisponibilidadeServico(calendarioService.estaDisponivel());
        if (respostaIndisponivel != null) {
            return respostaIndisponivel;
        }

        var retorno = calendarioService.criarEvento(request);
        return ResponseEntity.status(204).body(retorno);
    }

    @GetMapping("/{idEvento}")
    public ResponseEntity<?> obterEvento(@PathVariable String idEvento) {
        ResponseEntity<?> respostaIndisponivel = verificarDisponibilidadeServico(calendarioService.estaDisponivel());
        if (respostaIndisponivel != null) {
            return respostaIndisponivel;
        }

        var retorno = calendarioService.obterEvento(idEvento);
        return ResponseEntity.status(200).body(retorno);
    }

    @GetMapping
    public ResponseEntity<?> listarEventos() {
        ResponseEntity<?> respostaIndisponivel = verificarDisponibilidadeServico(calendarioService.estaDisponivel());
        if (respostaIndisponivel != null) {
            return respostaIndisponivel;
        }

        var retorno = calendarioService.listarEventos();
        return ResponseEntity.status(200).body(retorno);
    }

    @PutMapping("/{idEvento}")
    public ResponseEntity<?> atualizarEvento(
            @PathVariable String idEvento,
            @RequestBody CalendarEventRequest request) {
        ResponseEntity<?> respostaIndisponivel = verificarDisponibilidadeServico(calendarioService.estaDisponivel());
        if (respostaIndisponivel != null) {
            return respostaIndisponivel;
        }

        var retorno = calendarioService.atualizarEvento(idEvento, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idEvento}")
    public ResponseEntity<?> excluirEvento(@PathVariable String idEvento) {
        ResponseEntity<?> respostaIndisponivel = verificarDisponibilidadeServico(calendarioService.estaDisponivel());
        if (respostaIndisponivel != null) {
            return respostaIndisponivel;
        }
        calendarioService.excluirEvento(idEvento);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> verificarDisponibilidadeServico(boolean servicoDisponivel) {
        if (!servicoDisponivel) {
            ResponseEntity.status(503).build();
        }
        return null;
    }
}