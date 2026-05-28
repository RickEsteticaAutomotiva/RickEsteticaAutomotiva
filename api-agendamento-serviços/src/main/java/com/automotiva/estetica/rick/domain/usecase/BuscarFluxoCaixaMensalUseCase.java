package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.FluxoCaixaResumo;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarFluxoCaixaMensalUseCase {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final int PERCENTUAL_SCALE = 2;

    private final DashboardGateway dashboardGateway;

    public FluxoCaixaResumo execute() {
        PeriodoMensal periodo = PeriodoMensalFactory.ultimos30Dias();

        BigDecimal lucro = defaultValor(
                dashboardGateway.somarReceitaRecebidaDoPeriodo(periodo.inicio(), periodo.fim()));
        BigDecimal custo = defaultValor(
                dashboardGateway.somarCustoRealizadoDoPeriodo(periodo.inicio(), periodo.fim()));

        BigDecimal total = lucro.add(custo);
        BigDecimal percentualLucro = calcularPercentual(lucro, total);
        BigDecimal percentualCusto = total.compareTo(ZERO) == 0
                ? ZERO
                : CEM.subtract(percentualLucro).setScale(PERCENTUAL_SCALE, RoundingMode.HALF_UP);

        return new FluxoCaixaResumo(total, lucro, custo, percentualLucro, percentualCusto);
    }

    private BigDecimal calcularPercentual(BigDecimal parcela, BigDecimal total) {
        if (total.compareTo(ZERO) == 0) {
            return ZERO;
        }
        return parcela.multiply(CEM).divide(total, PERCENTUAL_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultValor(BigDecimal valor) {
        return valor == null ? ZERO : valor;
    }
}
