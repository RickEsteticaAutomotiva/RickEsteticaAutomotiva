package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.response.AgendamentosHojeListResponse;
import com.automotiva.estetica.rick.application.dto.response.HorarioDisponivelResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResponse;
import com.automotiva.estetica.rick.application.mapper.AgendamentoHojeMapper;
import com.automotiva.estetica.rick.application.security.ClienteOnly;
import com.automotiva.estetica.rick.application.security.OwnershipValidator;
import com.automotiva.estetica.rick.application.service.OrdemServicoApplicationService;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordem-servicos")
@RequiredArgsConstructor
@ClienteOnly
@Tag(name = "Ordens de Serviço", description = "Gerenciamento de ordens de serviço")
public class OrdemServicoController {

    private final OrdemServicoApplicationService ordemServicoUseCase;
    private final OwnershipValidator ownershipValidator;
    private final ItemServicoGateway itemServicoGateway;
    private final AgendamentoHojeMapper agendamentoHojeMapper;

    @GetMapping
    @Operation(summary = "Lista todas as ordens de serviço paginadas")
    public ResponseEntity<Page<OrdemServicoResponse>> buscarTodos(@Valid @ModelAttribute PageRequest pageRequest) {
        return ResponseEntity.ok(ordemServicoUseCase.buscarTodos(pageRequest));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca ordem de serviço por ID")
    public ResponseEntity<OrdemServicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoUseCase.buscarPorId(id));
    }

    @GetMapping("/usuario/{id}")
    @Operation(summary = "Busca ordens de serviço por usuário")
    public ResponseEntity<List<OrdemServicoResponse>> buscarPorUsuarioId(@PathVariable Long id) {
        ownershipValidator.validarPropriedade(id);
        return ResponseEntity.ok(ordemServicoUseCase.buscarPorUsuarioId(id));
    }

    @PostMapping
    @Operation(summary = "Cria uma nova ordem de serviço")
    public ResponseEntity<OrdemServicoResponse> criar(@Valid @RequestBody OrdemServicoRequest request) {
        return ResponseEntity.status(202).body(ordemServicoUseCase.criar(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza uma ordem de serviço")
    public ResponseEntity<OrdemServicoResponse> atualizar(@PathVariable Long id,
            @RequestBody OrdemServicoRequest request) {
        return ResponseEntity.ok(ordemServicoUseCase.atualizar(id, request));
    }

    @GetMapping("/horarios-disponiveis")
    @Operation(summary = "Busca horarios disponiveis para agendamento")
    public ResponseEntity<List<HorarioDisponivelResponse>> buscarHorariosDisponiveis(
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam("servicosIds") List<Long> servicosIds) {
        return ResponseEntity.ok(ordemServicoUseCase.buscarHorariosDisponiveis(data, servicosIds));
    }

    @GetMapping("/hoje")
    @Operation(summary = "Lista todos os agendamentos do dia atual com detalhes completos para visualização no dashboard")
    public ResponseEntity<AgendamentosHojeListResponse> buscarAgendamentosHoje() {
        return ResponseEntity.ok(ordemServicoUseCase.buscarAgendamentosHoje());
    }
}
