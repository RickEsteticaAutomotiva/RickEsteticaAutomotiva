package com.automotiva.estetica.rick.adapter.in.controller;

import com.automotiva.estetica.rick.application.dto.response.FaturamentoPeriodoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensConcluidasMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.TicketMedioMensalResponse;
import com.automotiva.estetica.rick.application.port.in.DashboardUseCase;
import com.automotiva.estetica.rick.infrastructure.security.GerenteOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@GerenteOnly
@Tag(name = "Dashboard", description = "Indicadores e métricas do negócio")
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    @GetMapping("/faturamento")
    @Operation(summary = "Retorna faturamento do mês atual com variação")
    public ResponseEntity<FaturamentoResponse> buscarFaturamentoTotal() {
        return ResponseEntity.ok(dashboardUseCase.buscarFaturamentoTotal());
    }

    @GetMapping("/total-ordens")
    @Operation(summary = "Retorna quantidade de ordens do mês com variação")
    public ResponseEntity<QtdOrdensMensalResponse> buscarQtdOrdensMes() {
        return ResponseEntity.ok(dashboardUseCase.buscarQtdTotalAgendamentosMes());
    }

    @GetMapping("/servicos-concluidos")
    @Operation(summary = "Retorna quantidade de ordens concluídas no mês")
    public ResponseEntity<QtdOrdensConcluidasMensalResponse> buscarTotalServicosConcluidosMes() {
        return ResponseEntity.ok(dashboardUseCase.buscarQtdOrdensConcluidasMes());
    }

    @GetMapping("/ticket-medio")
    @Operation(summary = "Retorna ticket médio do mês")
    public ResponseEntity<TicketMedioMensalResponse> buscarTicketMedioMes() {
        return ResponseEntity.ok(dashboardUseCase.buscarTicketMedioMes());
    }

    @GetMapping("/faturamento-periodo")
    @Operation(summary = "Retorna faturamento diário dos últimos 30 dias")
    public ResponseEntity<List<FaturamentoPeriodoResponse>> buscarFaturamentoPeriodo() {
        return ResponseEntity.ok(dashboardUseCase.buscarFaturamentoPeriodo());
    }
}
