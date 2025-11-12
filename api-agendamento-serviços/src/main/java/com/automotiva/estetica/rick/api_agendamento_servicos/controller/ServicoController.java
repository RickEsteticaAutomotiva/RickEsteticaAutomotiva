package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.page_request.DefaultPageRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.ServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @GetMapping
    public ResponseEntity<Page<ServicoDto>> buscarTodos(@Valid @ModelAttribute DefaultPageRequest pageRequest) {
        Page<ServicoDto> servicos = servicoService.buscarTodos(pageRequest);
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoDto> buscarPorId(@PathVariable Long id) {
        ServicoDto servico = servicoService.buscarPorId(id);
        return ResponseEntity.ok(servico);
    }

    @PostMapping
    public ResponseEntity<ServicoDto> criarServico(@RequestBody ServicoDto servicoDto) {
        ServicoDto servico = servicoService.criarServico(servicoDto);
        return ResponseEntity.status(201).body(servico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoDto> atualizarServico(@PathVariable Long id, @RequestBody ServicoDto servicoDto) {
        ServicoDto servicoAtualizado = servicoService.atualizarServico(id, servicoDto);
        return ResponseEntity.ok(servicoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarServico(@PathVariable Long id) {
        servicoService.deletarServico(id);
        return ResponseEntity.noContent().build();
    }
}
