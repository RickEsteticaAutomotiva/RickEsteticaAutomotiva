package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.*;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/faturamento")
    public ResponseEntity<FaturamentoResponseDto> buscarFaturamentoTotal() {
        var faturamento = dashboardService.buscarFaturamentoTotal();
        return ResponseEntity.ok(faturamento);
    }

    @GetMapping("/total-ordens")
    public ResponseEntity<QtdOrdensServicoMensalResponseDto> buscarQtdOrdensMes() {
        var totalMes = dashboardService.buscarQtdTotalAgendamentosMes();
        return ResponseEntity.ok(totalMes);
    }

    @GetMapping("/servicos-concluidos")
    public ResponseEntity<QtdOrdensServicoConcluidasMensalResponseDto> buscarTotalServicosConcluidosMes() {
        var totalOrdensConcluidasMes = dashboardService.buscarQtdOrdensConcluidasMes();
        return ResponseEntity.ok(totalOrdensConcluidasMes);
    }

    @GetMapping("/ticket-medio")
    public ResponseEntity<TicketMedioMensalResponseDto> buscarTicketMedioMes() {
        var totalTicketMedioMes = dashboardService.buscarTicketMedioMes();
        return ResponseEntity.ok(totalTicketMedioMes);
    }

    @GetMapping("/fluxo-caixa")
    public ResponseEntity<FluxoCaixaDto> fluxoCaixa() {
        FluxoCaixaDto response = dashboardService.fluxoCaixa();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/faturamento-servicos")
    public ResponseEntity<List<CategoriaDashboardDto>> faturamentoPorServicos(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano
    ) {
        return ResponseEntity.ok( dashboardService.calcularFaturamentoPorServico());
    }

    @GetMapping("/cancelamentos")
    public ResponseEntity<List<Map<String, Object>>> getCancelamentos() {
        List<Map<String, Object>> cancelamentos = dashboardService.buscarCancelamentos();
        return ResponseEntity.ok(cancelamentos);
    }
}
