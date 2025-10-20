package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CarrinhoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.CarrinhoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.CarrinhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carinhos")
@RequiredArgsConstructor
public class CarinhoController {
    private final CarrinhoService carrinhoService;

    @PostMapping
    public ResponseEntity<CarrinhoEntity> adicionarCarinho(@RequestBody CarrinhoDto carrinhoDto) {
        CarrinhoEntity criado = carrinhoService.adicionarCarrinho(carrinhoDto);
        return ResponseEntity.status(201).body(criado);
    }

    @DeleteMapping
    public ResponseEntity<Void> removerCarinho(@RequestBody CarrinhoDto carrinhoDto) {
        carrinhoService.removerCarrinho(carrinhoDto);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{idPessoa}")
    public ResponseEntity<List<ServicoDto>> listarServicosPorPessoa(@PathVariable Long idPessoa) {
        List<ServicoDto> servicos = carrinhoService.listarServicosPessoa(idPessoa);
        return ResponseEntity.ok(servicos);
    }
}
