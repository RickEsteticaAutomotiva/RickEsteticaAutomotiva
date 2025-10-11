package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ItemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.ItemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itens-servicos")
@RequiredArgsConstructor
public class ItemServicoController {

    private final ItemServicoService itemServicoService;

    @GetMapping
    public ResponseEntity<List<ItemServicoDto>> buscarTodos() {
        //TODO colocar paginado igual esta na pesssoaController
        List<ItemServicoDto> itens = itemServicoService.buscarTodos();
        return ResponseEntity.ok(itens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemServicoDto> buscarPorId(@PathVariable Long id) {
        ItemServicoDto item = itemServicoService.buscarPorId(id);
        return ResponseEntity.ok(item);
    }

    @PostMapping
    public ResponseEntity<ItemServicoDto> criarItemServico(@RequestBody ItemServicoDto itemServicoDto) {
        ItemServicoDto resposta = itemServicoService.criarItemServico(itemServicoDto);
        return ResponseEntity.status(201).body(resposta);
    }

    @GetMapping("/{idOrdem}/itens")
    public ResponseEntity<List<ItemServicoDto>> listarItens(@PathVariable Long idOrdem) {
        List<ItemServicoDto> itens = itemServicoService.listarPorOrdem(idOrdem);
        return ResponseEntity.ok(itens);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemServicoDto> atualizarItemServico(
            @PathVariable Long id,
            @RequestBody ItemServicoDto preco) {
        ItemServicoDto atualizado = itemServicoService.atualizarItemServico(id, preco);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarItemServico(@PathVariable Long id) {
        itemServicoService.deletarItemServico(id);
        return ResponseEntity.noContent().build();
    }
}
