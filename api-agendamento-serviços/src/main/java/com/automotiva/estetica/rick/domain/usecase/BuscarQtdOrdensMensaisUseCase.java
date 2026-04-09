package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.QtdOrdensMensalResumo;
import com.automotiva.estetica.rick.domain.entity.VariacaoPercentual;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarQtdOrdensMensaisUseCase {

    private final DashboardGateway dashboardGateway;

    public QtdOrdensMensalResumo execute() {
        PeriodoMensal mesAtual = PeriodoMensalFactory.mesAtual();
        PeriodoMensal mesAnterior = PeriodoMensalFactory.mesAnterior();

        Integer atual = dashboardGateway.buscarQtdOrdensDoMes(mesAtual.inicio(), mesAtual.fim());
        Integer anterior = dashboardGateway.buscarQtdOrdensDoMes(mesAnterior.inicio(), mesAnterior.fim());

        return new QtdOrdensMensalResumo(atual, VariacaoPercentual.calcular(atual, anterior));
    }
}
