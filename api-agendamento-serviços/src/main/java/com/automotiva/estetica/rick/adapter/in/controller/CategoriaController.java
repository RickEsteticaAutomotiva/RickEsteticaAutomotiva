package com.automotiva.estetica.rick.adapter.in.controller;

import com.automotiva.estetica.rick.application.dto.request.CategoriaRequest;
import com.automotiva.estetica.rick.application.dto.response.CategoriaResponse;
import com.automotiva.estetica.rick.application.port.in.CategoriaUseCase;
import com.automotiva.estetica.rick.infrastructure.security.ClienteOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Gerenciamento de categorias de serviço")
public class CategoriaController {

    private final CategoriaUseCase categoriaUseCase;

    @GetMapping
    @Operation(summary = "Lista todas as categorias")
    public ResponseEntity<List<CategoriaResponse>> buscarTodas() {
        return ResponseEntity.ok(categoriaUseCase.buscarTodas());
    }

    @PostMapping
    @ClienteOnly
    @Operation(summary = "Cria uma nova categoria")
    public ResponseEntity<Void> criar(@Valid @RequestBody CategoriaRequest request) {
        categoriaUseCase.criar(request);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping("/{id}")
    @ClienteOnly
    @Operation(summary = "Atualiza uma categoria")
    public ResponseEntity<CategoriaResponse> atualizar(
            @PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaUseCase.atualizar(id, request));
    }
}
