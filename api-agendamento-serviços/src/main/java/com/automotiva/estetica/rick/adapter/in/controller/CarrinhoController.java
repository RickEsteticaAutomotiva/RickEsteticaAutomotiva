package com.automotiva.estetica.rick.adapter.in.controller;

import com.automotiva.estetica.rick.application.dto.request.CarrinhoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoCarrinhoResponse;
import com.automotiva.estetica.rick.application.port.in.CarrinhoUseCase;
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
@RequestMapping("/carrinhos")
@RequiredArgsConstructor
@ClienteOnly
@Tag(name = "Carrinho", description = "Gerenciamento do carrinho de serviços")
public class CarrinhoController {

    private final CarrinhoUseCase carrinhoUseCase;

    @GetMapping("/pessoa/{idPessoa}")
    @Operation(summary = "Lista itens do carrinho de uma pessoa")
    public ResponseEntity<List<ServicoCarrinhoResponse>> listar(@PathVariable Long idPessoa) {
        return ResponseEntity.ok(carrinhoUseCase.listar(idPessoa));
    }

    @PostMapping
    @Operation(summary = "Adiciona serviço ao carrinho")
    public ResponseEntity<Void> adicionar(@Valid @RequestBody CarrinhoRequest request) {
        carrinhoUseCase.adicionar(request);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{idCarrinho}")
    @Operation(summary = "Remove item do carrinho")
    public ResponseEntity<Void> remover(@PathVariable Long idCarrinho) {
        carrinhoUseCase.remover(idCarrinho);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/pessoa/{idPessoa}/limpar")
    @Operation(summary = "Limpa todo o carrinho de uma pessoa")
    public ResponseEntity<Void> limpar(@PathVariable Long idPessoa) {
        carrinhoUseCase.limparCarrinhoPessoa(idPessoa);
        return ResponseEntity.noContent().build();
    }
}
