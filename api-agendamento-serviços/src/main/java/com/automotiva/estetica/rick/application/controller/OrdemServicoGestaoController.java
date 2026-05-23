package com.automotiva.estetica.rick.application.controller;


import com.automotiva.estetica.rick.application.dto.request.*;
import com.automotiva.estetica.rick.application.dto.response.AgendamentosHojeListResponse;
import com.automotiva.estetica.rick.application.dto.request.AdicionarServicosOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.AtualizarOrdemServicoGestaoRequest;
import com.automotiva.estetica.rick.application.dto.request.AtualizarValorServicoOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.OrdemServicoGestaoPageRequest;
import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoDetalheResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResumoResponse;
import com.automotiva.estetica.rick.application.security.GerenteOnly;
import com.automotiva.estetica.rick.application.service.OrdemServicoApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/ordem-servicos-gestao")
@RequiredArgsConstructor
@GerenteOnly
@Tag(name = "Gestão de Ordens de Serviço", description = "Endpoints para gerentes gerenciarem ordens de serviço")
public class OrdemServicoGestaoController {

    private final OrdemServicoApplicationService ordemServicoUseCase;

    @PostMapping
    @Operation(summary = "Cria uma nova ordem de serviço (gerente)")
    public ResponseEntity<OrdemServicoDetalheResponse> criarParaGestao(
            @Valid @RequestBody OrdemServicoRequest request) {
        return ResponseEntity.status(201).body(ordemServicoUseCase.criarParaGestao(request));
    }

    @GetMapping
    @Operation(summary = "Lista ordens de serviço com filtros para gestão")
    public ResponseEntity<Page<OrdemServicoResumoResponse>> buscarTodosParaGestao(
            @Valid @ModelAttribute OrdemServicoGestaoPageRequest request) {
        return ResponseEntity.ok(ordemServicoUseCase.buscarTodosParaGestao(request));
    }

    @GetMapping("/hoje")
    @Operation(summary = "Lista todos os agendamentos do dia atual com detalhes completos para visualização no dashboard")
    public ResponseEntity<AgendamentosHojeListResponse> buscarAgendamentosHoje() {
        return ResponseEntity.ok(ordemServicoUseCase.buscarAgendamentosHoje());
    }

    @GetMapping("/{ordemServicoId}")
    @Operation(summary = "Busca detalhes de uma ordem de serviço para gestão")
    public ResponseEntity<OrdemServicoDetalheResponse> buscarDetalheParaGestao(@PathVariable Long ordemServicoId) {
        return ResponseEntity.ok(ordemServicoUseCase.buscarDetalheParaGestao(ordemServicoId));
    }

    @PatchMapping("/{ordemServicoId}")
    @Operation(summary = "Atualiza uma ordem de serviço (data, observações e/ou status)")
    // Status possíveis:
    // 1 = AGUARDANDO (análise/confirmação)
    // 2 = EM_ANDAMENTO (em execução - notifica email)
    // 3 = AGUARDANDO_PECAS (peças/componentes)
    // 4 = CANCELADO
    // 5 = CONCLUIDO (notifica email)
    public ResponseEntity<OrdemServicoDetalheResponse> atualizarParaGestao(@PathVariable Long ordemServicoId,
            @Valid @RequestBody AtualizarOrdemServicoGestaoRequest request) {
        return ResponseEntity.ok(ordemServicoUseCase.atualizarParaGestao(ordemServicoId, request));
    }

    @PostMapping("/{ordemServicoId}/servicos")
    @Operation(summary = "Adiciona serviços a uma ordem de serviço")
    public ResponseEntity<OrdemServicoDetalheResponse> adicionarServicosParaGestao(@PathVariable Long ordemServicoId,
            @Valid @RequestBody AdicionarServicosOrdemRequest request) {
        return ResponseEntity.status(201)
                .body(ordemServicoUseCase.adicionarServicosParaGestao(ordemServicoId, request));
    }

    @PatchMapping("/{ordemServicoId}/servicos/{servicoId}")
    @Operation(summary = "Atualiza o valor aplicado de um serviço em uma ordem")
    public ResponseEntity<OrdemServicoDetalheResponse> atualizarValorServicoParaGestao(
            @PathVariable Long ordemServicoId, @PathVariable Long servicoId,
            @Valid @RequestBody AtualizarValorServicoOrdemRequest request) {
        return ResponseEntity
                .ok(ordemServicoUseCase.atualizarValorServicoParaGestao(ordemServicoId, servicoId, request));
    }

    @DeleteMapping("/{ordemServicoId}/servicos/{servicoId}")
    @Operation(summary = "Remove um serviço de uma ordem de serviço")
    public ResponseEntity<OrdemServicoDetalheResponse> removerServicoParaGestao(@PathVariable Long ordemServicoId,
            @PathVariable Long servicoId) {
        return ResponseEntity.ok(ordemServicoUseCase.removerServicoParaGestao(ordemServicoId, servicoId));
    }

    @PostMapping("/{ordemServicoId}/cancel")
    @Operation(summary = "Cancela uma ordem de serviço com motivo")
    public ResponseEntity<OrdemServicoDetalheResponse> cancelarParaGestao(@PathVariable Long ordemServicoId,
            @Valid @RequestBody CancelarOrdemRequest request) {
        return ResponseEntity.ok(ordemServicoUseCase.cancelarParaGestao(ordemServicoId, request));
    }
}
