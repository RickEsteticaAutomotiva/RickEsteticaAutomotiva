package com.automotiva.estetica.rick.api_agendamento_servicos.controller;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.BaseController;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/servicos")
public class ServicoController extends BaseController{
    @Autowired
    ServicoService ServicoService;

    @GetMapping
    public ResponseEntity buscarTodos() {
        var pessoas = ServicoService.buscarTodos();
        return definirRetorno(pessoas.getStatusCode(), pessoas.getObjeto(), pessoas.getMensagem());
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPessoaPorId(@PathVariable Long id) {
        RetornoComObjeto<ServicoDto> retorno = ServicoService.buscarPorId(id);
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    @PostMapping
    public ResponseEntity criarServico(@RequestBody ServicoDto ServicoDto) {
        var resposta = ServicoService.criarServico(ServicoDto);
        return definirRetorno(resposta.getStatusCode(), null, resposta.getMensagem());
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizarServico(
            @PathVariable Long id,
            @RequestBody ServicoDto pessoa) {
        var retorno = ServicoService.atualizarServico(id, pessoa);
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletarServico(@PathVariable Long id) {
        var retorno = ServicoService.deletarServico(id);
        return definirRetorno(retorno.getStatusCode(), null, retorno.getMensagem());
    }

}
