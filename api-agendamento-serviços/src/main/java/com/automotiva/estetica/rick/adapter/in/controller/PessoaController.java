package com.automotiva.estetica.rick.adapter.in.controller;

import com.automotiva.estetica.rick.application.dto.request.LoginRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaAtualizacaoRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaCadastroRequest;
import com.automotiva.estetica.rick.application.dto.request.SenhaRequest;
import com.automotiva.estetica.rick.application.dto.response.PessoaResponse;
import com.automotiva.estetica.rick.application.dto.response.TokenResponse;
import com.automotiva.estetica.rick.application.port.in.PessoaUseCase;
import com.automotiva.estetica.rick.infrastructure.security.ClienteOnly;
import com.automotiva.estetica.rick.infrastructure.security.OwnershipValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
@Tag(name = "Pessoas", description = "Gerenciamento de pessoas e autenticação")
public class PessoaController {

    private final PessoaUseCase pessoaUseCase;
    private final OwnershipValidator ownershipValidator;

    @GetMapping
    @ClienteOnly
    @Operation(summary = "Lista todas as pessoas paginadas")
    public ResponseEntity<Page<PessoaResponse>> buscarTodos(@Valid @ModelAttribute PageRequest pageRequest) {
        return ResponseEntity.ok(pessoaUseCase.buscarTodos(pageRequest));
    }

    @GetMapping("/{id}")
    @ClienteOnly
    @Operation(summary = "Busca pessoa por ID")
    public ResponseEntity<PessoaResponse> buscarPorId(@PathVariable Long id) {
        ownershipValidator.validarPropriedade(id);
        return ResponseEntity.ok(pessoaUseCase.buscarPorId(id));
    }

    @PostMapping("/")
    @Operation(summary = "Cadastra uma nova pessoa")
    public ResponseEntity<PessoaResponse> cadastrar(@Valid @RequestBody PessoaCadastroRequest request) {
        return ResponseEntity.status(201).body(pessoaUseCase.cadastrar(request));
    }

    @PutMapping("/{id}")
    @ClienteOnly
    @Operation(summary = "Atualiza dados de uma pessoa")
    public ResponseEntity<PessoaResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody PessoaAtualizacaoRequest request) {
        ownershipValidator.validarPropriedade(id);
        return ResponseEntity.ok(pessoaUseCase.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ClienteOnly
    @Operation(summary = "Remove uma pessoa")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ownershipValidator.validarPropriedade(id);
        pessoaUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Realiza login e retorna token JWT")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(pessoaUseCase.login(request));
    }

    @PatchMapping("/{id}/senha")
    @ClienteOnly
    @Operation(summary = "Atualiza a senha de uma pessoa")
    public ResponseEntity<Void> atualizarSenha(@PathVariable Long id, @Valid @RequestBody SenhaRequest request) {
        ownershipValidator.validarPropriedade(id);
        pessoaUseCase.atualizarSenha(id, request);
        return ResponseEntity.noContent().build();
    }
}
