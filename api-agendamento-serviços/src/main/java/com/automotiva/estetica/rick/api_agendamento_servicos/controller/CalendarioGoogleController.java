package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CalendarEventRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.BaseController;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.CalendarioGoogleService;
import com.google.api.services.calendar.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendario/eventos")
@RequiredArgsConstructor
public class CalendarioGoogleController extends BaseController {

    @Autowired
    private final CalendarioGoogleService calendarioService;

    @GetMapping("/saude")
    public ResponseEntity<?> verificarSaude() {
        boolean disponivel = calendarioService.estaDisponivel();
        if (disponivel) {
            return definirRetorno(200, null, "Serviço do Google Calendar está disponível");
        } else {
            return definirRetorno(503, null, "Serviço do Google Calendar não está disponível");
        }
    }

    @PostMapping
    public ResponseEntity<?> criarEvento(@RequestBody CalendarEventRequest request) {
        // Usando o novo método para verificar disponibilidade
        ResponseEntity<?> respostaIndisponivel = verificarDisponibilidadeServico(calendarioService.estaDisponivel());
        if (respostaIndisponivel != null) {
            return respostaIndisponivel;
        }

        var retorno = calendarioService.criarEvento(request);
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    @GetMapping("/{idEvento}")
    public ResponseEntity<?> obterEvento(@PathVariable String idEvento) {
        ResponseEntity<?> respostaIndisponivel = verificarDisponibilidadeServico(calendarioService.estaDisponivel());
        if (respostaIndisponivel != null) {
            return respostaIndisponivel;
        }

        var retorno = calendarioService.obterEvento(idEvento);
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    @GetMapping
    public ResponseEntity<?> listarEventos() {
        ResponseEntity<?> respostaIndisponivel = verificarDisponibilidadeServico(calendarioService.estaDisponivel());
        if (respostaIndisponivel != null) {
            return respostaIndisponivel;
        }

        var retorno = calendarioService.listarEventos();
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
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
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    @DeleteMapping("/{idEvento}")
    public ResponseEntity<?> excluirEvento(@PathVariable String idEvento) {
        ResponseEntity<?> respostaIndisponivel = verificarDisponibilidadeServico(calendarioService.estaDisponivel());
        if (respostaIndisponivel != null) {
            return respostaIndisponivel;
        }

        var retorno = calendarioService.excluirEvento(idEvento);
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    private ResponseEntity<?> verificarDisponibilidadeServico(boolean servicoDisponivel) {
        if (!servicoDisponivel) {
            return definirRetorno(503, null, "Serviço não está disponível");
        }
        return null;
    }
}