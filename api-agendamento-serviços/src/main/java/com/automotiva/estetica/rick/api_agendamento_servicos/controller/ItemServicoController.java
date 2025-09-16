package com.automotiva.estetica.rick.api_agendamento_servicos.controller;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ItemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.BaseController;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.ItemServicoService;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item-servico")
public class ItemServicoController extends BaseController{
    @Autowired
    ItemServicoService itemServicoService;

//    @GetMapping("/ordemServico")
//    public ResponseEntity buscarTodos() {
//        var pessoas = itemServicoService.buscarTodos();
//        return definirRetorno(pessoas.getStatusCode(), pessoas.getObjeto(), pessoas.getMensagem());
//    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPessoaPorId(@PathVariable Long id) {
        RetornoComObjeto<ItemServicoDto> retorno = itemServicoService.buscarPorId(id);
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    @PostMapping
    public ResponseEntity criarOrdemServico(@RequestBody ItemServicoDto itemServicoDto) {
        var resposta = itemServicoService.criarItemServico(itemServicoDto);
        return definirRetorno(resposta.getStatusCode(), null, resposta.getMensagem());
    }

    @GetMapping("/{idOrdem}/itens")
    public List<ItemServicoDto> listarItens(@PathVariable Long idOrdem) {
        return itemServicoService.listarPorOrdem(idOrdem);
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizarItemServico(
            @PathVariable Long id,
            @RequestBody ItemServicoDto preco) {
        var retorno = itemServicoService.atualizarItemServico(id, preco);
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletarItemServico(@PathVariable Long id) {
        var retorno = itemServicoService.deletarItemServico(id);
        return definirRetorno(retorno.getStatusCode(), null, retorno.getMensagem());
    }
}
