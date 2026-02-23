package com.automotiva.estetica.rick.adapter.in.controller;

import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResponse;
import com.automotiva.estetica.rick.application.port.in.OrdemServicoUseCase;
import com.automotiva.estetica.rick.infrastructure.security.ClienteOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/ordem-servicos")
@RequiredArgsConstructor
@ClienteOnly
@Tag(name = "Ordens de Serviço", description = "Gerenciamento de ordens de serviço")
public class OrdemServicoController {

    private final OrdemServicoUseCase ordemServicoUseCase;

    @GetMapping
    @Operation(summary = "Lista todas as ordens de serviço paginadas")
    public ResponseEntity<Page<OrdemServicoResponse>> buscarTodos(@Valid @ModelAttribute PageRequest pageRequest) {
        return ResponseEntity.ok(ordemServicoUseCase.buscarTodos(pageRequest));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca ordem de serviço por ID")
    public ResponseEntity<OrdemServicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoUseCase.buscarPorId(id));
    }

    @GetMapping("/usuario/{id}")
    @Operation(summary = "Busca ordens de serviço por usuário")
    public ResponseEntity<List<OrdemServicoResponse>> buscarPorUsuarioId(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoUseCase.buscarPorUsuarioId(id));
    }

    @PostMapping
    @Operation(summary = "Cria uma nova ordem de serviço")
    public ResponseEntity<OrdemServicoResponse> criar(@Valid @RequestBody OrdemServicoRequest request) {
        return ResponseEntity.status(201).body(ordemServicoUseCase.criar(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza uma ordem de serviço")
    public ResponseEntity<OrdemServicoResponse> atualizar(
            @PathVariable Long id, @RequestBody OrdemServicoRequest request) {
        return ResponseEntity.ok(ordemServicoUseCase.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma ordem de serviço")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ordemServicoUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
