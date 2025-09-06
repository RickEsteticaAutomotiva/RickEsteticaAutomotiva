package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.BaseController;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordem-servico")
public class OrdemServicoController extends BaseController {

    @Autowired
    OrdemServicoService ordemServicoService;

    @GetMapping
    public ResponseEntity buscarTodos() {
        var pessoas = ordemServicoService.buscarTodos();
        return definirRetorno(pessoas.getStatusCode(), pessoas.getObjeto(), pessoas.getMensagem());
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPessoaPorId(@PathVariable Long id) {
        RetornoComObjeto<OrdemServicoDto> retorno = ordemServicoService.buscarPorId(id);
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    @PostMapping("/")
    public ResponseEntity criarOrdemServico(@RequestBody OrdemServicoDto ordemServicoDto) {
        var resposta = ordemServicoService.criarOrdemServico(ordemServicoDto);
        return definirRetorno(resposta.getStatusCode(), null, resposta.getMensagem());
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizarOrdemServico(
            @PathVariable Long id,
            @RequestBody OrdemServicoDto pessoa) {
        var retorno = ordemServicoService.atualizarOrdemServico(id, pessoa);
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletarOrdemServico(@PathVariable Long id) {
        var retorno = ordemServicoService.deletarOrdemServico(id);
        return definirRetorno(retorno.getStatusCode(), null, retorno.getMensagem());
    }
}
