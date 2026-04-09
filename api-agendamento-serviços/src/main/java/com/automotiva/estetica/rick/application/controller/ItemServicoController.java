package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.dto.response.ItemServicoResponse;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.usecase.BuscarItemServicoPorIdUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarItensServicoPorOrdemUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarItensServicoUseCase;
import com.automotiva.estetica.rick.application.security.ClienteOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    private final ListarItensServicoUseCase listarItensServicoUseCase;
    private final BuscarItemServicoPorIdUseCase buscarItemServicoPorIdUseCase;
    private final ListarItensServicoPorOrdemUseCase listarItensServicoPorOrdemUseCase;

    @GetMapping
    @Operation(summary = "Lista todos os itens de serviço")
    public ResponseEntity<List<ItemServicoResponse>> buscarTodos() {
        List<ItemServicoResponse> itens = listarItensServicoUseCase.execute().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(itens);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca item de serviço por ID")
    public ResponseEntity<ItemServicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(buscarItemServicoPorIdUseCase.execute(id)));
    }

    @GetMapping("/ordem/{idOrdem}")
    @Operation(summary = "Lista itens de serviço por ordem")
    public ResponseEntity<List<ItemServicoResponse>> listarPorOrdem(@PathVariable Long idOrdem) {
        List<ItemServicoResponse> itens = listarItensServicoPorOrdemUseCase.execute(idOrdem).stream()
                .map(this::toResponse).toList();
        return ResponseEntity.ok(itens);
    }

    private ItemServicoResponse toResponse(ItemServico itemServico) {
        return ItemServicoResponse.builder().id(itemServico.getId())
                .idServico(itemServico.getServico() != null ? itemServico.getServico().getId() : null)
                .idOrdemServico(itemServico.getOrdemServico() != null ? itemServico.getOrdemServico().getId() : null)
                .preco(itemServico.getPreco()).build();
    }
}
