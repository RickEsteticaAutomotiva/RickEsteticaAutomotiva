package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.BaseController;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
