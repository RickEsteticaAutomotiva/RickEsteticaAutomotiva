package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoPageRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.OrdemServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordem-servicos")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    @GetMapping
    public ResponseEntity<Page<OrdemServicoDto>> buscarTodosPaginado(@Valid @ModelAttribute OrdemServicoPageRequest pageRequest) {
        Page<OrdemServicoDto> ordemServicos = ordemServicoService.buscarTodos(pageRequest);
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

    @PatchMapping("/{id}")
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
