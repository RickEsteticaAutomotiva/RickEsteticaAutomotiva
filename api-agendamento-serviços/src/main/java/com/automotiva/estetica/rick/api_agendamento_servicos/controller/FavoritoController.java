package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.FavoritoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoFavoritoDto;
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

    @DeleteMapping("/{idFavorito}")
    public ResponseEntity<Void> removerFavorito(@PathVariable Long idFavorito) {
        favoritoService.removerFavorito(idFavorito);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idPessoa}")
    public ResponseEntity<List<ServicoFavoritoDto>> listarFavoritosPessoa(@PathVariable Long idPessoa) {
        var servicos = favoritoService.listarServicosPessoa(idPessoa);
        return ResponseEntity.ok(servicos);
    }
}
