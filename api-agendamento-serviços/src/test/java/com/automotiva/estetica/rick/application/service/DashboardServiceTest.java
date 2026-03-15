package com.automotiva.estetica.rick.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.response.FaturamentoResponse;
import com.automotiva.estetica.rick.application.port.out.OrdemServicoRepositoryPort;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @Test
    @DisplayName("Deve calcular variação percentual positiva corretamente")
    void buscarFaturamentoTotal_variacaoPositiva() {
        when(ordemServicoRepositoryPort.somarFaturamentoDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.valueOf(1000), BigDecimal.valueOf(500));

        FaturamentoResponse result = dashboardService.buscarFaturamentoTotal();

        assertEquals(0, new BigDecimal("100.0000").compareTo(result.getVariacaoPercentual()));
        assertEquals(BigDecimal.valueOf(1000), result.getFaturamentoAtual());
    }

    @Test
    @DisplayName("Deve calcular variação percentual negativa corretamente")
    void buscarFaturamentoTotal_variacaoNegativa() {
        when(ordemServicoRepositoryPort.somarFaturamentoDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.valueOf(500), BigDecimal.valueOf(1000));

        FaturamentoResponse result = dashboardService.buscarFaturamentoTotal();

        assertEquals(0, new BigDecimal("-50.0000").compareTo(result.getVariacaoPercentual()));
    }

    @Test
    @DisplayName("Deve retornar variação zero quando faturamento anterior for zero")
    void buscarFaturamentoTotal_faturamentoAnteriorZero() {
        when(ordemServicoRepositoryPort.somarFaturamentoDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.valueOf(1000), BigDecimal.ZERO);

        FaturamentoResponse result = dashboardService.buscarFaturamentoTotal();

        assertEquals(BigDecimal.ZERO, result.getVariacaoPercentual());
    }

    @Test
    @DisplayName("Deve retornar variação zero quando faturamento atual e anterior forem zero")
    void buscarFaturamentoTotal_ambosZero() {
        when(ordemServicoRepositoryPort.somarFaturamentoDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.ZERO, BigDecimal.ZERO);

        FaturamentoResponse result = dashboardService.buscarFaturamentoTotal();

        assertEquals(BigDecimal.ZERO, result.getVariacaoPercentual());
    }
}
