package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.PageableFactory;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.ServicoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoResponse;
import com.automotiva.estetica.rick.application.mapper.ServicoDTOMapper;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.usecase.AtualizarServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarServicoPorIdUseCase;
import com.automotiva.estetica.rick.domain.usecase.CadastrarServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.DeletarServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarServicosUseCase;
import com.automotiva.estetica.rick.application.security.ClienteOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
@Tag(name = "Serviços", description = "Gerenciamento de serviços")
public class ServicoController {

    private final CadastrarServicoUseCase cadastrarServicoUseCase;
    private final BuscarServicoPorIdUseCase buscarServicoPorIdUseCase;
    private final ListarServicosUseCase listarServicosUseCase;
    private final AtualizarServicoUseCase atualizarServicoUseCase;
    private final DeletarServicoUseCase deletarServicoUseCase;
    private final ServicoDTOMapper servicoDTOMapper;

    @GetMapping
    @Operation(summary = "Lista todos os serviços paginados")
    public ResponseEntity<Page<ServicoResponse>> buscarTodos(@Valid @ModelAttribute PageRequest pageRequest) {
        Pageable pageable = PageableFactory.from(pageRequest);
        Page<ServicoResponse> pagina = listarServicosUseCase.execute(pageRequest.getFiltro(), pageable)
                .map(servicoDTOMapper::toResponse);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca serviço por ID")
    public ResponseEntity<ServicoResponse> buscarPorId(@PathVariable Long id) {
        Servico servico = buscarServicoPorIdUseCase.execute(id);
        return ResponseEntity.ok(servicoDTOMapper.toResponse(servico));
    }

    @PostMapping
    @ClienteOnly
    @Operation(summary = "Cria um novo serviço")
    public ResponseEntity<ServicoResponse> criar(@Valid @RequestBody ServicoRequest request) {
        Servico servico = servicoDTOMapper.toDomain(request);
        Servico servicoCriado = cadastrarServicoUseCase.execute(servico);
        return ResponseEntity.status(201).body(servicoDTOMapper.toResponse(servicoCriado));
    }

    @PatchMapping("/{id}")
    @ClienteOnly
    @Operation(summary = "Atualiza um serviço")
    public ResponseEntity<ServicoResponse> atualizar(@PathVariable Long id, @RequestBody ServicoRequest request) {
        Integer duracaoMinutos = servicoDTOMapper.horasParaMinutos(request.getDuracaoHoras());
        Servico servico = atualizarServicoUseCase.execute(id, request.getNome(), request.getDescricao(),
                request.getPreco(), request.getImagem(), request.getCategoriaId(), duracaoMinutos);
        return ResponseEntity.ok(servicoDTOMapper.toResponse(servico));
    }

    @DeleteMapping("/{id}")
    @ClienteOnly
    @Operation(summary = "Remove um serviço")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarServicoUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
