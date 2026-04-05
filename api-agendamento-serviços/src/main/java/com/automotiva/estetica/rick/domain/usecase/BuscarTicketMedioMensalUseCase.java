package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.TicketMedioMensalResumo;
import com.automotiva.estetica.rick.domain.entity.VariacaoPercentual;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarTicketMedioMensalUseCase {

    private final DashboardGateway dashboardGateway;

    public TicketMedioMensalResumo execute() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        BigDecimal atual = dashboardGateway.calcularTicketMedioDoMes(mesAtual.inicio(), mesAtual.fim());
        BigDecimal anterior = dashboardGateway.calcularTicketMedioDoMes(mesAnterior.inicio(), mesAnterior.fim());

        return new TicketMedioMensalResumo(atual, VariacaoPercentual.calcular(atual, anterior));
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
