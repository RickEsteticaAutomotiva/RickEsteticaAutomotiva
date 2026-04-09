package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.QtdOrdensConcluidasMensalResumo;
import com.automotiva.estetica.rick.domain.entity.VariacaoPercentual;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarQtdOrdensConcluidasMensaisUseCase {

    private final DashboardGateway dashboardGateway;

    public QtdOrdensConcluidasMensalResumo execute() {
        PeriodoMensal mesAtual = PeriodoMensal.mesAtual();
        PeriodoMensal mesAnterior = PeriodoMensal.mesAnterior();

        Integer atual = dashboardGateway.buscarQtdOrdensConcluidasNoMes(mesAtual.inicio(), mesAtual.fim());
        Integer anterior = dashboardGateway.buscarQtdOrdensConcluidasNoMes(mesAnterior.inicio(), mesAnterior.fim());

        return new QtdOrdensConcluidasMensalResumo(atual, VariacaoPercentual.calcular(atual, anterior));
    }
}
