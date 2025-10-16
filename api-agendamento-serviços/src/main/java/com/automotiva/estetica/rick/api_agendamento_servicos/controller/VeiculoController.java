package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.VeiculoDto;

import com.automotiva.estetica.rick.api_agendamento_servicos.service.VeiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veiculos")
@RequiredArgsConstructor
public class VeiculoController {

    private final VeiculoService veiculoService;

    @PostMapping
    public ResponseEntity<VeiculoDto> cadastrarVeiculo(@RequestBody VeiculoDto veiculoDto) {
        VeiculoDto resposta = veiculoService.cadastrarVeiculo(veiculoDto);
        return ResponseEntity.status(201).body(resposta);
    }

    @GetMapping
    public ResponseEntity<List<VeiculoDto>> buscarTodosVeiculos() {
        //TODO colocar paginado igual esta na pesssoaController
        List<VeiculoDto> resposta = veiculoService.buscarTodosVeiculos();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<VeiculoDto>> buscarVeiculosByPessoaId(@PathVariable Long id) {
        List<VeiculoDto> resposta = veiculoService.buscarVeiculosByPessoaId(id);
        return ResponseEntity.ok(resposta);
    }

    @PatchMapping()
    public ResponseEntity<Void> atualizarVeiculo(@RequestBody VeiculoDto veiculoDto) {
        veiculoService.atualizarVeiculo(veiculoDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVeiculoPorId(@PathVariable Long id) {
        veiculoService.deletarVeiculo(id);
        return ResponseEntity.noContent().build();
    }
}
