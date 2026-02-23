package com.automotiva.estetica.rick.adapter.in.controller;

import com.automotiva.estetica.rick.application.dto.request.FavoritoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoFavoritoResponse;
import com.automotiva.estetica.rick.application.port.in.FavoritoUseCase;
import com.automotiva.estetica.rick.infrastructure.security.ClienteOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
@ClienteOnly
@Tag(name = "Favoritos", description = "Gerenciamento de serviços favoritos")
public class FavoritoController {

    private final FavoritoUseCase favoritoUseCase;

    @GetMapping("/pessoa/{idPessoa}")
    @Operation(summary = "Lista favoritos de uma pessoa")
    public ResponseEntity<List<ServicoFavoritoResponse>> listar(@PathVariable Long idPessoa) {
        return ResponseEntity.ok(favoritoUseCase.listar(idPessoa));
    }

    @PostMapping
    @Operation(summary = "Adiciona serviço aos favoritos")
    public ResponseEntity<Void> adicionar(@Valid @RequestBody FavoritoRequest request) {
        favoritoUseCase.adicionar(request);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{idFavorito}")
    @Operation(summary = "Remove serviço dos favoritos")
    public ResponseEntity<Void> remover(@PathVariable Long idFavorito) {
        favoritoUseCase.remover(idFavorito);
        return ResponseEntity.noContent().build();
    }
}
