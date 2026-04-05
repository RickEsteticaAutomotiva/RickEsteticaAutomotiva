package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.FaturamentoPeriodoResumo;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarFaturamentoPeriodoUseCase {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final DashboardGateway dashboardGateway;

    public List<FaturamentoPeriodoResumo> execute() {
        LocalDateTime dataInicial = LocalDate.now().minusDays(30).atStartOfDay();

        return dashboardGateway.buscarFaturamentoPorDia(dataInicial).stream()
                .map(dto -> new FaturamentoPeriodoResumo(dto.dia(), defaultValor(dto.totalDia()))).toList();
    }

    private BigDecimal defaultValor(BigDecimal valor) {
        return valor == null ? ZERO : valor;
    }
}
