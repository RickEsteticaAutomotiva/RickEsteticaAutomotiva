package com.automotiva.estetica.rick.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.response.CancelamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoPeriodoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoResponse;
import com.automotiva.estetica.rick.application.dto.response.FluxoCaixaResponse;
import com.automotiva.estetica.rick.application.dto.response.HomeResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensConcluidasMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.TicketMedioMensalResponse;
import com.automotiva.estetica.rick.application.service.DashboardApplicationService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de DashboardController")
class DashboardControllerTest {

    @Mock
    private DashboardApplicationService dashboardUseCase;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    @DisplayName("buscarFaturamentoTotal deve delegar e retornar 200")
    void buscarFaturamentoTotal_deveDelegarERetornar200() {
        FaturamentoResponse esperado = FaturamentoResponse.builder().faturamentoAtual(new BigDecimal("500.00")).build();
        when(dashboardUseCase.buscarFaturamentoTotal()).thenReturn(esperado);

        var response = dashboardController.buscarFaturamentoTotal();

        verify(dashboardUseCase).buscarFaturamentoTotal();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }

    @Test
    @DisplayName("buscarQtdOrdensMes deve delegar e retornar 200")
    void buscarQtdOrdensMes_deveDelegarERetornar200() {
        QtdOrdensMensalResponse esperado = QtdOrdensMensalResponse.builder().totalOrdens(11).build();
        when(dashboardUseCase.buscarQtdTotalAgendamentosMes()).thenReturn(esperado);

        var response = dashboardController.buscarQtdOrdensMes();

        verify(dashboardUseCase).buscarQtdTotalAgendamentosMes();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }

    @Test
    @DisplayName("buscarTotalServicosConcluidosMes deve delegar e retornar 200")
    void buscarTotalServicosConcluidosMes_deveDelegarERetornar200() {
        QtdOrdensConcluidasMensalResponse esperado = QtdOrdensConcluidasMensalResponse.builder()
                .totalOrdensConcluidas(7).build();
        when(dashboardUseCase.buscarQtdOrdensConcluidasMes()).thenReturn(esperado);

        var response = dashboardController.buscarTotalServicosConcluidosMes();

        verify(dashboardUseCase).buscarQtdOrdensConcluidasMes();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }

    @Test
    @DisplayName("buscarTicketMedioMes deve delegar e retornar 200")
    void buscarTicketMedioMes_deveDelegarERetornar200() {
        TicketMedioMensalResponse esperado = TicketMedioMensalResponse.builder()
                .totalTicketMedioMesAtual(new BigDecimal("120.00")).build();
        when(dashboardUseCase.buscarTicketMedioMes()).thenReturn(esperado);

        var response = dashboardController.buscarTicketMedioMes();

        verify(dashboardUseCase).buscarTicketMedioMes();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }

    @Test
    @DisplayName("buscarFaturamentoPeriodo deve delegar e retornar 200")
    void buscarFaturamentoPeriodo_deveDelegarERetornar200() {
        List<FaturamentoPeriodoResponse> esperado = List.of(FaturamentoPeriodoResponse.builder().build());
        when(dashboardUseCase.buscarFaturamentoPeriodo()).thenReturn(esperado);

        var response = dashboardController.buscarFaturamentoPeriodo();

        verify(dashboardUseCase).buscarFaturamentoPeriodo();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }

    @Test
    @DisplayName("buscarFaturamentoServicos deve delegar e retornar 200")
    void buscarFaturamentoServicos_deveDelegarERetornar200() {
        List<FaturamentoServicoResponse> esperado = List
                .of(FaturamentoServicoResponse.builder().categoria("Lavagem").build());
        when(dashboardUseCase.buscarFaturamentoServicos()).thenReturn(esperado);

        var response = dashboardController.buscarFaturamentoServicos();

        verify(dashboardUseCase).buscarFaturamentoServicos();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }

    @Test
    @DisplayName("buscarFluxoCaixa deve delegar e retornar 200")
    void buscarFluxoCaixa_deveDelegarERetornar200() {
        FluxoCaixaResponse esperado = FluxoCaixaResponse.builder().total(new BigDecimal("1000.00")).build();
        when(dashboardUseCase.buscarFluxoCaixa()).thenReturn(esperado);

        var response = dashboardController.buscarFluxoCaixa();

        verify(dashboardUseCase).buscarFluxoCaixa();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }

    @Test
    @DisplayName("buscarCancelamentos deve delegar e retornar 200")
    void buscarCancelamentos_deveDelegarERetornar200() {
        List<CancelamentoResponse> esperado = List.of(CancelamentoResponse.builder().tipo("falta_peca").build());
        when(dashboardUseCase.buscarCancelamentos()).thenReturn(esperado);

        var response = dashboardController.buscarCancelamentos();

        verify(dashboardUseCase).buscarCancelamentos();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }

    @Test
    @DisplayName("buscarHomeResumo deve delegar e retornar 200")
    void buscarHomeResumo_deveDelegarERetornar200() {
        HomeResumoResponse esperado = HomeResumoResponse.builder().agendamentosHoje(3L).build();
        when(dashboardUseCase.buscarHomeResumo()).thenReturn(esperado);

        var response = dashboardController.buscarHomeResumo();

        verify(dashboardUseCase).buscarHomeResumo();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }
}
