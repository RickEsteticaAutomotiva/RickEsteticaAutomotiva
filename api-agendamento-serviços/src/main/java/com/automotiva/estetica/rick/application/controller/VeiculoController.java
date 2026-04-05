package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.dto.request.VeiculoRequest;
import com.automotiva.estetica.rick.application.dto.response.VeiculoResponse;
import com.automotiva.estetica.rick.application.mapper.VeiculoDTOMapper;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.usecase.AtualizarVeiculoUseCase;
import com.automotiva.estetica.rick.domain.usecase.CadastrarVeiculoUseCase;
import com.automotiva.estetica.rick.domain.usecase.DeletarVeiculoUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarVeiculosPorPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarVeiculosUseCase;
import com.automotiva.estetica.rick.application.security.ClienteOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/veiculos")
@RequiredArgsConstructor
@ClienteOnly
@Tag(name = "Veículos", description = "Gerenciamento de veículos")
public class VeiculoController {

    private final CadastrarVeiculoUseCase cadastrarVeiculoUseCase;
    private final ListarVeiculosUseCase listarVeiculosUseCase;
    private final ListarVeiculosPorPessoaUseCase listarVeiculosPorPessoaUseCase;
    private final AtualizarVeiculoUseCase atualizarVeiculoUseCase;
    private final DeletarVeiculoUseCase deletarVeiculoUseCase;
    private final VeiculoDTOMapper veiculoDTOMapper;

    @GetMapping
    @Operation(summary = "Lista todos os veículos")
    public ResponseEntity<List<VeiculoResponse>> buscarTodos() {
        List<VeiculoResponse> veiculos = listarVeiculosUseCase.execute().stream().map(veiculoDTOMapper::toResponse)
                .toList();
        return ResponseEntity.ok(veiculos);
    }

    @GetMapping("/pessoa/{id}")
    @Operation(summary = "Busca veículos por pessoa")
    public ResponseEntity<List<VeiculoResponse>> buscarPorPessoa(@PathVariable Long id) {
        List<VeiculoResponse> veiculos = listarVeiculosPorPessoaUseCase.execute(id).stream()
                .map(veiculoDTOMapper::toResponse).toList();
        return ResponseEntity.ok(veiculos);
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo veículo")
    public ResponseEntity<VeiculoResponse> cadastrar(@Valid @RequestBody VeiculoRequest request) {
        Veiculo veiculo = veiculoDTOMapper.toDomain(request);
        veiculo.setPessoa(Pessoa.builder().id(request.getIdPessoa()).build());
        Veiculo veiculoCriado = cadastrarVeiculoUseCase.execute(veiculo);
        return ResponseEntity.status(201).body(veiculoDTOMapper.toResponse(veiculoCriado));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza um veículo")
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody VeiculoRequest request) {
        atualizarVeiculoUseCase.execute(id, request.getPlaca(), request.getModelo(), request.getMarca(),
                request.getPorte(), request.getCor(), request.getAno());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um veículo")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarVeiculoUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
