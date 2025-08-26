package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.LoginDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaCadastroDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.BaseController;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pessoa")
public class PessoaController extends BaseController {

    @Autowired
    PessoaService pessoaService;

    @GetMapping
    public ResponseEntity buscarTodos() {
        var pessoas = pessoaService.buscarTodos();
        return definirRetorno(pessoas.getStatusCode(), pessoas.getObjeto(), pessoas.getMensagem());
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto) {
        var respota = pessoaService.login(loginDto);
        return definirRetorno(respota.getStatusCode(), respota.getObjeto(), respota.getMensagem());
    }

    @PostMapping("/cadastro")
    public ResponseEntity cadastro(@RequestBody PessoaCadastroDto pessoaCadastroDto) {
        var respota = pessoaService.cadastro(pessoaCadastroDto);
        return definirRetorno(respota.getStatusCode(), null, respota.getMensagem());
    }
}
