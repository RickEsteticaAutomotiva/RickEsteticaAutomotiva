package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.dto.response.CancelamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoPeriodoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoResponse;
import com.automotiva.estetica.rick.application.dto.response.FluxoCaixaResponse;
import com.automotiva.estetica.rick.application.dto.response.HomeResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensConcluidasMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.TicketMedioMensalResponse;
import com.automotiva.estetica.rick.application.service.DashboardApplicationService;
import com.automotiva.estetica.rick.application.security.GerenteOnly;
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

    private final DashboardApplicationService dashboardUseCase;

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

    @GetMapping("/faturamento-servicos")
    @Operation(summary = "Retorna o faturamento por serviço do mês atual")
    public ResponseEntity<List<FaturamentoServicoResponse>> buscarFaturamentoServicos() {
        return ResponseEntity.ok(dashboardUseCase.buscarFaturamentoServicos());
    }

    @GetMapping("/fluxo-caixa")
    @Operation(summary = "Retorna o fluxo de caixa consolidado dos últimos 30 dias")
    public ResponseEntity<FluxoCaixaResponse> buscarFluxoCaixa() {
        return ResponseEntity.ok(dashboardUseCase.buscarFluxoCaixa());
    }

    @GetMapping("/cancelamentos")
    @Operation(summary = "Retorna cancelamentos agrupados por motivo no mês atual")
    public ResponseEntity<List<CancelamentoResponse>> buscarCancelamentos() {
        return ResponseEntity.ok(dashboardUseCase.buscarCancelamentos());
    }

    @GetMapping("/home-resumo")
    @Operation(summary = "Retorna o resumo da home do gerente")
    public ResponseEntity<HomeResumoResponse> buscarHomeResumo() {
        return ResponseEntity.ok(dashboardUseCase.buscarHomeResumo());
    }
}
