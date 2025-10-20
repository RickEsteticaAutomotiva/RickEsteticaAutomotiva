package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CarrinhoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.CarrinhoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrinhos")
@RequiredArgsConstructor
public class CarrinhoController {
    private final CarrinhoService carrinhoService;

    @PostMapping
    public ResponseEntity<Void> adicionarCarrinho(@RequestBody @Valid CarrinhoDto carrinhoDto) {
        carrinhoService.adicionarCarrinho(carrinhoDto);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removerCarrinho(@RequestBody @Valid CarrinhoDto carrinhoDto) {
        carrinhoService.removerCarrinho(carrinhoDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idPessoa}")
    public ResponseEntity<List<ServicoDto>> listarServicosPessoa(@PathVariable Long idPessoa) {
        List<ServicoDto> servicos = carrinhoService.listarServicosPessoa(idPessoa);
        return ResponseEntity.ok(servicos);
    }
}
