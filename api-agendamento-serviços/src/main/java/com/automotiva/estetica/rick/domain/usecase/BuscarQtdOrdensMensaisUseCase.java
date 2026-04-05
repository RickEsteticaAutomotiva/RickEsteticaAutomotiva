package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.QtdOrdensMensalResumo;
import com.automotiva.estetica.rick.domain.entity.VariacaoPercentual;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarQtdOrdensMensaisUseCase {

    private final DashboardGateway dashboardGateway;

    public QtdOrdensMensalResumo execute() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        Integer atual = dashboardGateway.buscarQtdOrdensDoMes(mesAtual.inicio(), mesAtual.fim());
        Integer anterior = dashboardGateway.buscarQtdOrdensDoMes(mesAnterior.inicio(), mesAnterior.fim());

        return new QtdOrdensMensalResumo(atual, VariacaoPercentual.calcular(atual, anterior));
    }

    private PeriodoMensal getPeriodoMesAtual() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        return new PeriodoMensal(inicio.atStartOfDay(), LocalDateTime.now());
    }

    private PeriodoMensal getPeriodoMesAnterior() {
        LocalDate inicio = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate fim = inicio.plusMonths(1).minusDays(1);
        return new PeriodoMensal(inicio.atStartOfDay(), fim.atTime(23, 59, 59));
    }

    private record PeriodoMensal(LocalDateTime inicio, LocalDateTime fim) {
    }
}
