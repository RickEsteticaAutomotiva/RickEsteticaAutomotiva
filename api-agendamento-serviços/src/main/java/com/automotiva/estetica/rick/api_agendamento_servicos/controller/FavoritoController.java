package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.FavoritoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.FavoritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
public class FavoritoController {
    private final FavoritoService favoritoService;

    @PostMapping
    public ResponseEntity<Void> adicionarFavorito(@RequestBody @Valid FavoritoDto favoritoDto) {
        favoritoService.adicionarFavorito(favoritoDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removerFavorito(@RequestBody @Valid FavoritoDto favoritoDto) {
        favoritoService.removerCarinho(favoritoDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pessoa/{idPessoa}")
    public ResponseEntity<List<ServicoDto>> listarServicosPessoa(@PathVariable Long idPessoa) {
        List<ServicoDto> servicos = favoritoService.listarServicosPessoa(idPessoa);
        return ResponseEntity.ok(servicos);
    }
}
