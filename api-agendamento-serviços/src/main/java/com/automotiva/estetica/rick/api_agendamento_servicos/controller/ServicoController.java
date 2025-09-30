package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.ServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @GetMapping
    public ResponseEntity<List<ServicoDto>> buscarTodos() {
        List<ServicoDto> servicos = servicoService.buscarTodos();
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
    public ResponseEntity<ServicoDto> atualizarServico(
            @PathVariable Long id,
            @RequestBody ServicoDto servicoDto) {
        ServicoDto servicoAtualizado = servicoService.atualizarServico(id, servicoDto);
        return ResponseEntity.ok(servicoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarServico(@PathVariable Long id) {
        servicoService.deletarServico(id);
        return ResponseEntity.noContent().build();
    }
}
