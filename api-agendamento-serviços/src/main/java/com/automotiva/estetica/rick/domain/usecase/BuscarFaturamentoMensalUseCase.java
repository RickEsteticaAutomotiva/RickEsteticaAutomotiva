package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.FaturamentoMensalResumo;
import com.automotiva.estetica.rick.domain.entity.VariacaoPercentual;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarFaturamentoMensalUseCase {

    private final DashboardGateway dashboardGateway;

    public FaturamentoMensalResumo execute() {
        PeriodoMensal mesAtual = PeriodoMensal.mesAtual();
        PeriodoMensal mesAnterior = PeriodoMensal.mesAnterior();

        BigDecimal atual = dashboardGateway.somarFaturamentoDoPeriodo(mesAtual.inicio(), mesAtual.fim());
        BigDecimal anterior = dashboardGateway.somarFaturamentoDoPeriodo(mesAnterior.inicio(), mesAnterior.fim());

        return new FaturamentoMensalResumo(atual, VariacaoPercentual.calcular(atual, anterior));
    }
}
