package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CarinhoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.Carinho;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.CarinhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carinhos")
@RequiredArgsConstructor
public class CarinhoController {
    private final CarinhoService carinhoService;

    @PostMapping
    public ResponseEntity<Carinho> adicionarCarinho(@RequestBody CarinhoDto carinhoDto) {
        Carinho criado = carinhoService.adicionarCarinho(carinhoDto);
        return ResponseEntity.status(201).body(criado);
    }

    @DeleteMapping
    public ResponseEntity<Void> removerCarinho(@RequestBody CarinhoDto carinhoDto) {
        carinhoService.removerCarinho(carinhoDto);
        return ResponseEntity.noContent().build();
    }
}
