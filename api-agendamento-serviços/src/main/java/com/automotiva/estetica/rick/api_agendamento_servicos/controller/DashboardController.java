package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.*;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.DashboardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/faturamento-periodo")
    public ResponseEntity<List<FaturamentoPeriodoResponseDto>> buscarFaturamentoPeriodo() {
        var dadosFaturamento = dashboardService.buscarFaturamentoPeriodo();
        return ResponseEntity.ok(dadosFaturamento);
    }
}
