// src/main/java/com/automotiva/estetica/rick/api_agendamento_servicos/controller/PessoaController.java
package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.LoginDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaCadastroDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.PessoaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService pessoaService;

    @GetMapping()
    public ResponseEntity<List<PessoaDto>> buscarTodosPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(defaultValue = "id") String ordenarPor) {

        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(ordenarPor));
        List<PessoaDto> pessoas = pessoaService.buscarTodos(pageable);
        return ResponseEntity.ok(pessoas);
    }

    @PostMapping("/login")
    public ResponseEntity<PessoaDto> login(@RequestBody LoginDto loginDto) {
        PessoaDto pessoa = pessoaService.login(loginDto);
        return ResponseEntity.ok(pessoa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaDto> buscarPessoaPorId(@PathVariable Long id) {
        PessoaDto pessoa = pessoaService.buscarPorId(id);
        return ResponseEntity.ok(pessoa);
    }

    @PostMapping("")
    public ResponseEntity<PessoaCadastroDto> criarPessoa(@RequestBody PessoaCadastroDto pessoaCadastroDto) {
        PessoaCadastroDto pessoa = pessoaService.criarPessoa(pessoaCadastroDto);
        return ResponseEntity.status(201).body(pessoa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaCadastroDto> atualizarPessoa(
            @PathVariable Long id,
            @RequestBody PessoaCadastroDto pessoa) {
        PessoaCadastroDto pessoaAtualizada = pessoaService.atualizarPessoa(id, pessoa);
        return ResponseEntity.ok(pessoaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPessoa(@PathVariable Long id) {
        pessoaService.deletarPessoa(id);
        return ResponseEntity.noContent().build();
    }
}
