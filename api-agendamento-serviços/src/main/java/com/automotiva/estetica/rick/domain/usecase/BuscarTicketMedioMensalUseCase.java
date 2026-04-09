package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.TicketMedioMensalResumo;
import com.automotiva.estetica.rick.domain.entity.VariacaoPercentual;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarTicketMedioMensalUseCase {

    private final DashboardGateway dashboardGateway;

    public TicketMedioMensalResumo execute() {
        PeriodoMensal mesAtual = PeriodoMensal.mesAtual();
        PeriodoMensal mesAnterior = PeriodoMensal.mesAnterior();

        BigDecimal atual = dashboardGateway.calcularTicketMedioDoMes(mesAtual.inicio(), mesAtual.fim());
        BigDecimal anterior = dashboardGateway.calcularTicketMedioDoMes(mesAnterior.inicio(), mesAnterior.fim());

        return new TicketMedioMensalResumo(atual, VariacaoPercentual.calcular(atual, anterior));
    }
}
