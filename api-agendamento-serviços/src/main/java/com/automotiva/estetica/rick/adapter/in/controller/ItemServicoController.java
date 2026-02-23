package com.automotiva.estetica.rick.adapter.in.controller;

import com.automotiva.estetica.rick.application.dto.response.ItemServicoResponse;
import com.automotiva.estetica.rick.application.port.in.ItemServicoUseCase;
import com.automotiva.estetica.rick.infrastructure.security.ClienteOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/itens-servico")
@RequiredArgsConstructor
@ClienteOnly
@Tag(name = "Itens de Serviço", description = "Gerenciamento de itens vinculados às ordens de serviço")
public class ItemServicoController {

    private final ItemServicoUseCase itemServicoUseCase;

    @GetMapping
    @Operation(summary = "Lista todos os itens de serviço")
    public ResponseEntity<List<ItemServicoResponse>> buscarTodos() {
        return ResponseEntity.ok(itemServicoUseCase.buscarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca item de serviço por ID")
    public ResponseEntity<ItemServicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemServicoUseCase.buscarPorId(id));
    }

    @GetMapping("/ordem/{idOrdem}")
    @Operation(summary = "Lista itens de serviço por ordem")
    public ResponseEntity<List<ItemServicoResponse>> listarPorOrdem(@PathVariable Long idOrdem) {
        return ResponseEntity.ok(itemServicoUseCase.listarPorOrdem(idOrdem));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um item de serviço")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        itemServicoUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
