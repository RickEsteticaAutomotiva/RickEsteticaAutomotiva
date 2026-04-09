package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.dto.request.CarrinhoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoCarrinhoResponse;
import com.automotiva.estetica.rick.domain.usecase.AdicionarCarrinhoUseCase;
import com.automotiva.estetica.rick.domain.usecase.LimparCarrinhoPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarCarrinhoPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.RemoverCarrinhoUseCase;
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
@RequestMapping("/carrinhos")
@RequiredArgsConstructor
@ClienteOnly
@Tag(name = "Carrinho", description = "Gerenciamento do carrinho de serviços")
public class CarrinhoController {

    private final AdicionarCarrinhoUseCase adicionarCarrinhoUseCase;
    private final RemoverCarrinhoUseCase removerCarrinhoUseCase;
    private final LimparCarrinhoPessoaUseCase limparCarrinhoPessoaUseCase;
    private final ListarCarrinhoPessoaUseCase listarCarrinhoPessoaUseCase;

    @GetMapping("/pessoa/{idPessoa}")
    @Operation(summary = "Lista itens do carrinho de uma pessoa")
    public ResponseEntity<List<ServicoCarrinhoResponse>> listar(@PathVariable Long idPessoa) {
        List<ServicoCarrinhoResponse> carrinho = listarCarrinhoPessoaUseCase.execute(idPessoa).stream()
                .map(c -> ServicoCarrinhoResponse.builder().idCarrinho(c.getId()).idServico(c.getServico().getId())
                        .nome(c.getServico().getNome()).descricao(c.getServico().getDescricao())
                        .preco(c.getServico().getPreco()).imagem(c.getServico().getImagem()).build())
                .toList();
        return ResponseEntity.ok(carrinho);
    }

    @PostMapping
    @Operation(summary = "Adiciona serviço ao carrinho")
    public ResponseEntity<Void> adicionar(@Valid @RequestBody CarrinhoRequest request) {
        adicionarCarrinhoUseCase.execute(request.getIdPessoa(), request.getIdServico());
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{idCarrinho}")
    @Operation(summary = "Remove item do carrinho")
    public ResponseEntity<Void> remover(@PathVariable Long idCarrinho) {
        removerCarrinhoUseCase.execute(idCarrinho);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/pessoa/{idPessoa}/limpar")
    @Operation(summary = "Limpa todo o carrinho de uma pessoa")
    public ResponseEntity<Void> limpar(@PathVariable Long idPessoa) {
        limparCarrinhoPessoaUseCase.execute(idPessoa);
        return ResponseEntity.noContent().build();
    }
}
