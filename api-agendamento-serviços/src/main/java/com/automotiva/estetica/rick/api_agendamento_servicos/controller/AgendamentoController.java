package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.AgendamentoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.BaseController;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController extends BaseController {

    @Autowired
    AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity realizarAgendamento(@RequestBody AgendamentoDto agendamentoDto) {
        var resposta = agendamentoService.cadastrarAgendamento(agendamentoDto);
        return definirRetorno(resposta.getStatusCode(), null, resposta.getMensagem());
    }

    @GetMapping
    public ResponseEntity consultarAgendamentos() {
        var resposta = agendamentoService.buscarAgendamentos();
        return definirRetorno(resposta.getStatusCode(), resposta.getObjeto(), resposta.getMensagem());
    }

    @GetMapping("/pessoa/{id}")
    public ResponseEntity consultarAgendamentosByPessoaId(@PathVariable Long id) {
        var resposta = agendamentoService.buscarAgendamentosByPessoaId(id);
        return definirRetorno(resposta.getStatusCode(), resposta.getObjeto(), resposta.getMensagem());
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity atualizarAgendamento(@PathVariable Long id, @RequestBody AgendamentoDto agendamentoDto) {
        var resposta = agendamentoService.atualizarAgendamento(id, agendamentoDto);
        return definirRetorno(resposta.getStatusCode(), null, resposta.getMensagem());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletarAgendamento(@PathVariable Long id) {
        var resposta = agendamentoService.deletarAgendamentos(id);
        return definirRetorno(resposta.getStatusCode(), null, resposta.getMensagem());
    }
}
