package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.dto.request.CategoriaRequest;
import com.automotiva.estetica.rick.application.dto.response.CategoriaResponse;
import com.automotiva.estetica.rick.application.mapper.CategoriaDTOMapper;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.usecase.AtualizarCategoriaUseCase;
import com.automotiva.estetica.rick.domain.usecase.CadastrarCategoriaUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarCategoriasUseCase;
import com.automotiva.estetica.rick.application.security.ClienteOnly;
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

    private final CadastrarCategoriaUseCase cadastrarCategoriaUseCase;
    private final ListarCategoriasUseCase listarCategoriasUseCase;
    private final AtualizarCategoriaUseCase atualizarCategoriaUseCase;
    private final CategoriaDTOMapper categoriaDTOMapper;

    @GetMapping
    @Operation(summary = "Lista todas as categorias")
    public ResponseEntity<List<CategoriaResponse>> buscarTodas() {
        List<CategoriaResponse> categorias = listarCategoriasUseCase.execute().stream()
                .map(categoriaDTOMapper::toResponse).toList();
        return ResponseEntity.ok(categorias);
    }

    @PostMapping
    @ClienteOnly
    @Operation(summary = "Cria uma nova categoria")
    public ResponseEntity<Void> criar(@Valid @RequestBody CategoriaRequest request) {
        Categoria categoria = categoriaDTOMapper.toDomain(request);
        cadastrarCategoriaUseCase.execute(categoria);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping("/{id}")
    @ClienteOnly
    @Operation(summary = "Atualiza uma categoria")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request) {
        Categoria categoria = atualizarCategoriaUseCase.execute(id, request.getNome());
        return ResponseEntity.ok(categoriaDTOMapper.toResponse(categoria));
    }
}
