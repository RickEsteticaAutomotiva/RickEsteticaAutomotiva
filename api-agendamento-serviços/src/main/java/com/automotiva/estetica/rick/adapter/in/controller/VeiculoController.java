package com.automotiva.estetica.rick.adapter.in.controller;

import com.automotiva.estetica.rick.application.dto.request.VeiculoRequest;
import com.automotiva.estetica.rick.application.dto.response.VeiculoResponse;
import com.automotiva.estetica.rick.application.port.in.VeiculoUseCase;
import com.automotiva.estetica.rick.infrastructure.security.ClienteOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/veiculos")
@RequiredArgsConstructor
@ClienteOnly
@Tag(name = "Veículos", description = "Gerenciamento de veículos")
public class VeiculoController {

    private final VeiculoUseCase veiculoUseCase;

    @GetMapping
    @Operation(summary = "Lista todos os veículos")
    public ResponseEntity<List<VeiculoResponse>> buscarTodos() {
        return ResponseEntity.ok(veiculoUseCase.buscarTodos());
    }

    @GetMapping("/pessoa/{id}")
    @Operation(summary = "Busca veículos por pessoa")
    public ResponseEntity<List<VeiculoResponse>> buscarPorPessoa(@PathVariable Long id) {
        return ResponseEntity.ok(veiculoUseCase.buscarPorPessoaId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo veículo")
    public ResponseEntity<VeiculoResponse> cadastrar(@Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.status(201).body(veiculoUseCase.cadastrar(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza um veículo")
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody VeiculoRequest request) {
        veiculoUseCase.atualizar(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um veículo")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        veiculoUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
