package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.LoginDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaCadastroDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.BaseController;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComObjeto;
import org.springframework.http.HttpHeaders;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pessoa")
public class PessoaController extends BaseController {

    @Autowired
    PessoaService pessoaService;

    @GetMapping("/paginado")
    public ResponseEntity buscarTodosPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(defaultValue = "id") String ordenarPor) {

        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(ordenarPor));
        var resposta = pessoaService.buscarTodos(pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Pagina-Atual", String.valueOf(resposta.getPaginaAtual()));
        headers.add("X-Total-Paginas", String.valueOf(resposta.getTotalPaginas()));
        headers.add("X-Total-Elementos", String.valueOf(resposta.getTotalElementos()));
        headers.add("X-Tamanho-Pagina", String.valueOf(resposta.getTamanhoPagina()));
        headers.add("X-Ultima-Pagina", String.valueOf(resposta.isUltimaPagina()));
        headers.add("X-Primeira-Pagina", String.valueOf(resposta.isPrimeiraPagina()));
        headers.add("X-Mensagem", resposta.getMensagem());

        var responseEntity = definirRetorno(resposta.getStatusCode(), resposta.getConteudo(), resposta.getMensagem());

        return ResponseEntity.status(responseEntity.getStatusCode())
                .headers(headers)
                .body(responseEntity.getBody());
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto) {
        var resposta = pessoaService.login(loginDto);
        return definirRetorno(resposta.getStatusCode(), resposta.getObjeto(), resposta.getMensagem());
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPessoaPorId(@PathVariable Long id) {
        RetornoComObjeto<PessoaDto> retorno = pessoaService.buscarPorId(id);
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    @PostMapping("/")
    public ResponseEntity criarPessoa(@RequestBody PessoaCadastroDto pessoaCadastroDto) {
        var resposta = pessoaService.criarPessoa(pessoaCadastroDto);
        return definirRetorno(resposta.getStatusCode(), null, resposta.getMensagem());
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizarPessoa(
            @PathVariable Long id,
            @RequestBody PessoaCadastroDto pessoa) {
        var retorno = pessoaService.atualizarPessoa(id, pessoa);
        return definirRetorno(retorno.getStatusCode(), retorno.getObjeto(), retorno.getMensagem());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletarPessoa(@PathVariable Long id) {
        var retorno = pessoaService.deletarPessoa(id);
        return definirRetorno(retorno.getStatusCode(), null, retorno.getMensagem());
    }
}
