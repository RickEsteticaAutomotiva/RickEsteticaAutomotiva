// src/main/java/com/automotiva/estetica/rick/api_agendamento_servicos/controller/PessoaController.java
package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.*;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.PessoaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService pessoaService;

    @SecurityRequirement(name = "Bearer")
    @GetMapping
    public ResponseEntity<Page<PessoaDto>> buscarTodosPaginado(@Valid @ModelAttribute PessoaPageRequest pageRequest) {
        Page<PessoaDto> pessoas = pessoaService.buscarTodosComFiltro(pageRequest);
        return ResponseEntity.ok(pessoas);
    }

    @PostMapping("/login")
    public ResponseEntity<PessoaTokenDto> login(@RequestBody LoginDto loginDto) {
        PessoaTokenDto pessoa = pessoaService.login(loginDto);
        return ResponseEntity.ok(pessoa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaDto> buscarPessoaPorId(@PathVariable Long id) {
        PessoaDto pessoa = pessoaService.buscarPorId(id);
        return ResponseEntity.ok(pessoa);
    }

    @SecurityRequirement(name = "Bearer")
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
