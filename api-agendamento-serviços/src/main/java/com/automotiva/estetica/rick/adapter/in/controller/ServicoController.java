package com.automotiva.estetica.rick.adapter.in.controller;

import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.ServicoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoResponse;
import com.automotiva.estetica.rick.application.port.in.ServicoUseCase;
import com.automotiva.estetica.rick.infrastructure.security.ClienteOnly;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
@Tag(name = "Serviços", description = "Gerenciamento de serviços")
public class ServicoController {

    private final ServicoUseCase servicoUseCase;

    @GetMapping
    @Operation(summary = "Lista todos os serviços paginados")
    public ResponseEntity<Page<ServicoResponse>> buscarTodos(
            @Valid @ModelAttribute PageRequest pageRequest) {
        return ResponseEntity.ok(servicoUseCase.buscarTodos(pageRequest));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca serviço por ID")
    public ResponseEntity<ServicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicoUseCase.buscarPorId(id));
    }

    @PostMapping
    @ClienteOnly
    @Operation(summary = "Cria um novo serviço")
    public ResponseEntity<ServicoResponse> criar(@Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.status(201).body(servicoUseCase.criar(request));
    }

    @PatchMapping("/{id}")
    @ClienteOnly
    @Operation(summary = "Atualiza um serviço")
    public ResponseEntity<ServicoResponse> atualizar(
            @PathVariable Long id, @RequestBody ServicoRequest request) {
        return ResponseEntity.ok(servicoUseCase.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ClienteOnly
    @Operation(summary = "Remove um serviço")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        servicoUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
