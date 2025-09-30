package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.OrdemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordem-servicos")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    @GetMapping
    public ResponseEntity<List<OrdemServicoDto>> buscarTodos() {
        List<OrdemServicoDto> ordemServicos = ordemServicoService.buscarTodos();
        return ResponseEntity.ok(ordemServicos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoDto> buscarPorId(@PathVariable Long id) {
        OrdemServicoDto ordemServico = ordemServicoService.buscarPorId(id);
        return ResponseEntity.ok(ordemServico);
    }

    @PostMapping
    public ResponseEntity<OrdemServicoDto> criarOrdemServico(@RequestBody OrdemServicoDto ordemServicoDto) {
        OrdemServicoDto ordemServico = ordemServicoService.criarOrdemServico(ordemServicoDto);
        return ResponseEntity.status(201).body(ordemServico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoDto> atualizarOrdemServico(
            @PathVariable Long id,
            @RequestBody OrdemServicoDto ordemServico) {
        OrdemServicoDto ordemServicoAtualizada = ordemServicoService.atualizarOrdemServico(id, ordemServico);
        return ResponseEntity.ok(ordemServicoAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOrdemServico(@PathVariable Long id) {
        ordemServicoService.deletarOrdemServico(id);
        return ResponseEntity.noContent().build();
    }
}
