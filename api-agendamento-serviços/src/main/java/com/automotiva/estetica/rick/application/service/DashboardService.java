package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.dto.response.FaturamentoDiarioDto;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoPeriodoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensConcluidasMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.TicketMedioMensalResponse;
import com.automotiva.estetica.rick.application.port.in.DashboardUseCase;
import com.automotiva.estetica.rick.application.port.out.OrdemServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.VariacaoPercentual;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService implements DashboardUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @Override
    public FaturamentoResponse buscarFaturamentoTotal() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        BigDecimal atual = ordemServicoRepositoryPort.somarFaturamentoDoPeriodo(mesAtual.inicio(), mesAtual.fim());
        BigDecimal anterior =
                ordemServicoRepositoryPort.somarFaturamentoDoPeriodo(mesAnterior.inicio(), mesAnterior.fim());

        return FaturamentoResponse.builder()
                .faturamentoAtual(atual)
                .variacaoPercentual(VariacaoPercentual.calcular(atual, anterior))
                .build();
    }

    @Override
    public QtdOrdensMensalResponse buscarQtdTotalAgendamentosMes() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        Integer atual = ordemServicoRepositoryPort.buscarQtdOrdensDoMes(mesAtual.inicio(), mesAtual.fim());
        Integer anterior = ordemServicoRepositoryPort.buscarQtdOrdensDoMes(mesAnterior.inicio(), mesAnterior.fim());

        return QtdOrdensMensalResponse.builder()
                .totalOrdens(atual)
                .variacaoPercentual(VariacaoPercentual.calcular(atual, anterior))
                .build();
    }

    @Override
    public QtdOrdensConcluidasMensalResponse buscarQtdOrdensConcluidasMes() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        Integer atual = ordemServicoRepositoryPort.buscarQtdOrdensConcluidasNoMes(mesAtual.inicio(), mesAtual.fim());
        Integer anterior =
                ordemServicoRepositoryPort.buscarQtdOrdensConcluidasNoMes(mesAnterior.inicio(), mesAnterior.fim());

        return QtdOrdensConcluidasMensalResponse.builder()
                .totalOrdensConcluidas(atual)
                .variacaoPercentual(VariacaoPercentual.calcular(atual, anterior))
                .build();
    }

    @Override
    public TicketMedioMensalResponse buscarTicketMedioMes() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        BigDecimal atual = ordemServicoRepositoryPort.calcularTicketMedioDoMes(mesAtual.inicio(), mesAtual.fim());
        BigDecimal anterior =
                ordemServicoRepositoryPort.calcularTicketMedioDoMes(mesAnterior.inicio(), mesAnterior.fim());

        return TicketMedioMensalResponse.builder()
                .totalTicketMedioMesAtual(atual)
                .variacaoPercentual(VariacaoPercentual.calcular(atual, anterior))
                .build();
    }

    @Override
    public List<FaturamentoPeriodoResponse> buscarFaturamentoPeriodo() {
        LocalDateTime dataInicial = LocalDate.now().minusDays(30).atStartOfDay();
        List<FaturamentoDiarioDto> rows = ordemServicoRepositoryPort.buscarFaturamentoPorDia(dataInicial);
        return rows.stream()
                .map(dto -> FaturamentoPeriodoResponse.builder()
                        .dia(dto.dia())
                        .totalDia(dto.totalDia())
                        .build())
                .toList();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private PeriodoMensal getPeriodoMesAtual() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        return new PeriodoMensal(inicio.atStartOfDay(), LocalDateTime.now());
    }

    private PeriodoMensal getPeriodoMesAnterior() {
        LocalDate inicio = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate fim = inicio.plusMonths(1).minusDays(1);
        return new PeriodoMensal(inicio.atStartOfDay(), fim.atTime(23, 59, 59));
    }

    private record PeriodoMensal(LocalDateTime inicio, LocalDateTime fim) {}
}
