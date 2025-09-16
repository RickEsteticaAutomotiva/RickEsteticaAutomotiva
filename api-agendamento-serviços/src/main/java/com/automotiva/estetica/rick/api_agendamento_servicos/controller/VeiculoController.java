package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.VeiculoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.BaseController;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController extends BaseController {

    @Autowired
    VeiculoService veiculoService;

    @PostMapping()
    public ResponseEntity cadastrarVeiculo(@RequestBody VeiculoDto veiculoDto) {
        var resposta = veiculoService.cadastrarVeiculo(veiculoDto);
        return definirRetorno(resposta.getStatusCode(), null, resposta.getMensagem());
    }

    @GetMapping()
    public ResponseEntity buscarTodosVeiculos() {
        var resposta = veiculoService.buscarTodosVeiculos();
        return definirRetorno(resposta.getStatusCode(), resposta.getObjeto(), resposta.getMensagem());
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarVeiculosByPessoaId(@PathVariable Long id) {
        var resposta = veiculoService.buscarVeiculosByPessoaId(id);
        return definirRetorno(resposta.getStatusCode(), resposta.getObjeto(), resposta.getMensagem());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletarVeiculoPorId(@PathVariable Long id) {
        var resposta = veiculoService.deletarVeiculo(id);
        return definirRetorno(resposta.getStatusCode(), null, resposta.getMensagem());
    }
}
