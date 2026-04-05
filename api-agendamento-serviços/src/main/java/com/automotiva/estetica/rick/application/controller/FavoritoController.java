package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.dto.request.FavoritoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoFavoritoResponse;
import com.automotiva.estetica.rick.domain.usecase.AdicionarFavoritoUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarFavoritoPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.RemoverFavoritoUseCase;
import com.automotiva.estetica.rick.application.security.ClienteOnly;
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

    private final AdicionarFavoritoUseCase adicionarFavoritoUseCase;
    private final RemoverFavoritoUseCase removerFavoritoUseCase;
    private final ListarFavoritoPessoaUseCase listarFavoritoPessoaUseCase;

    @GetMapping("/pessoa/{idPessoa}")
    @Operation(summary = "Lista favoritos de uma pessoa")
    public ResponseEntity<List<ServicoFavoritoResponse>> listar(@PathVariable Long idPessoa) {
        List<ServicoFavoritoResponse> favoritos = listarFavoritoPessoaUseCase.execute(idPessoa).stream()
                .map(f -> ServicoFavoritoResponse.builder().idFavorito(f.getId()).idServico(f.getServico().getId())
                        .nome(f.getServico().getNome()).descricao(f.getServico().getDescricao())
                        .preco(f.getServico().getPreco()).imagem(f.getServico().getImagem()).build())
                .toList();
        return ResponseEntity.ok(favoritos);
    }

    @PostMapping
    @Operation(summary = "Adiciona serviço aos favoritos")
    public ResponseEntity<Void> adicionar(@Valid @RequestBody FavoritoRequest request) {
        adicionarFavoritoUseCase.execute(request.getIdPessoa(), request.getIdServico());
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{idFavorito}")
    @Operation(summary = "Remove serviço dos favoritos")
    public ResponseEntity<Void> remover(@PathVariable Long idFavorito) {
        removerFavoritoUseCase.execute(idFavorito);
        return ResponseEntity.noContent().build();
    }
}
