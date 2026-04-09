package com.automotiva.estetica.rick.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.assembler.DashboardResponseAssembler;
import com.automotiva.estetica.rick.application.dto.response.CancelamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoPeriodoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoResponse;
import com.automotiva.estetica.rick.application.dto.response.FluxoCaixaResponse;
import com.automotiva.estetica.rick.application.dto.response.HomeResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensConcluidasMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.TicketMedioMensalResponse;
import com.automotiva.estetica.rick.domain.entity.CancelamentoResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoMensalResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoPeriodoResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoCategoriaResumo;
import com.automotiva.estetica.rick.domain.entity.FluxoCaixaResumo;
import com.automotiva.estetica.rick.domain.entity.ProximoAgendamentoResumo;
import com.automotiva.estetica.rick.domain.entity.QtdOrdensConcluidasMensalResumo;
import com.automotiva.estetica.rick.domain.entity.QtdOrdensMensalResumo;
import com.automotiva.estetica.rick.domain.entity.TicketMedioMensalResumo;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import com.automotiva.estetica.rick.domain.usecase.BuscarFaturamentoMensalUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarFluxoCaixaMensalUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarCancelamentosMensaisUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarQtdOrdensConcluidasMensaisUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarQtdOrdensMensaisUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarTicketMedioMensalUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarFaturamentoPeriodoUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarFaturamentoServicosMensalUseCase;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
class DashboardApplicationServiceTest {

    @Spy
    private DashboardResponseAssembler dashboardResponseAssembler = new DashboardResponseAssembler();

    @InjectMocks
    private DashboardApplicationService dashboardService;

    @Mock
    private DashboardGateway dashboardGateway;

    @Mock
    private BuscarFaturamentoMensalUseCase buscarFaturamentoMensalUseCase;

    @Mock
    private BuscarQtdOrdensMensaisUseCase buscarQtdOrdensMensaisUseCase;

    @Mock
    private BuscarQtdOrdensConcluidasMensaisUseCase buscarQtdOrdensConcluidasMensaisUseCase;

    @Mock
    private BuscarTicketMedioMensalUseCase buscarTicketMedioMensalUseCase;

    @Mock
    private BuscarFluxoCaixaMensalUseCase buscarFluxoCaixaMensalUseCase;

    @Mock
    private BuscarCancelamentosMensaisUseCase buscarCancelamentosMensaisUseCase;

    @Mock
    private BuscarFaturamentoPeriodoUseCase buscarFaturamentoPeriodoUseCase;

    @Mock
    private BuscarFaturamentoServicosMensalUseCase buscarFaturamentoServicosMensalUseCase;

    @Test
    @DisplayName("Deve delegar faturamento mensal para use case dedicado")
    void buscarFaturamentoTotal_delegacaoUseCase() {
        FaturamentoResponse esperado = FaturamentoResponse.builder().faturamentoAtual(BigDecimal.valueOf(1000))
                .variacaoPercentual(new BigDecimal("25.0000")).build();
        when(buscarFaturamentoMensalUseCase.execute()).thenReturn(
                new FaturamentoMensalResumo(esperado.getFaturamentoAtual(), esperado.getVariacaoPercentual()));

        FaturamentoResponse result = dashboardService.buscarFaturamentoTotal();

        assertEquals(0, esperado.getFaturamentoAtual().compareTo(result.getFaturamentoAtual()));
        assertEquals(0, esperado.getVariacaoPercentual().compareTo(result.getVariacaoPercentual()));
    }

    @Test
    @DisplayName("Deve delegar quantidade mensal de ordens para use case dedicado")
    void buscarQtdTotalAgendamentosMes_delegacaoUseCase() {
        QtdOrdensMensalResponse esperado = QtdOrdensMensalResponse.builder().totalOrdens(12)
                .variacaoPercentual(new BigDecimal("20.0000")).build();
        when(buscarQtdOrdensMensaisUseCase.execute())
                .thenReturn(new QtdOrdensMensalResumo(esperado.getTotalOrdens(), esperado.getVariacaoPercentual()));

        QtdOrdensMensalResponse result = dashboardService.buscarQtdTotalAgendamentosMes();

        assertEquals(esperado.getTotalOrdens(), result.getTotalOrdens());
        assertEquals(0, esperado.getVariacaoPercentual().compareTo(result.getVariacaoPercentual()));
    }

    @Test
    @DisplayName("Deve delegar total mensal de ordens concluidas para use case dedicado")
    void buscarQtdOrdensConcluidasMes_delegacaoUseCase() {
        QtdOrdensConcluidasMensalResponse esperado = QtdOrdensConcluidasMensalResponse.builder()
                .totalOrdensConcluidas(8).variacaoPercentual(new BigDecimal("14.2857")).build();
        when(buscarQtdOrdensConcluidasMensaisUseCase.execute()).thenReturn(new QtdOrdensConcluidasMensalResumo(
                esperado.getTotalOrdensConcluidas(), esperado.getVariacaoPercentual()));

        QtdOrdensConcluidasMensalResponse result = dashboardService.buscarQtdOrdensConcluidasMes();

        assertEquals(esperado.getTotalOrdensConcluidas(), result.getTotalOrdensConcluidas());
        assertEquals(0, esperado.getVariacaoPercentual().compareTo(result.getVariacaoPercentual()));
    }

    @Test
    @DisplayName("Deve delegar ticket medio mensal para use case dedicado")
    void buscarTicketMedioMes_delegacaoUseCase() {
        TicketMedioMensalResponse esperado = TicketMedioMensalResponse.builder()
                .totalTicketMedioMesAtual(BigDecimal.valueOf(300)).variacaoPercentual(new BigDecimal("50.0000"))
                .build();
        when(buscarTicketMedioMensalUseCase.execute()).thenReturn(
                new TicketMedioMensalResumo(esperado.getTotalTicketMedioMesAtual(), esperado.getVariacaoPercentual()));

        TicketMedioMensalResponse result = dashboardService.buscarTicketMedioMes();

        assertEquals(0, esperado.getTotalTicketMedioMesAtual().compareTo(result.getTotalTicketMedioMesAtual()));
        assertEquals(0, esperado.getVariacaoPercentual().compareTo(result.getVariacaoPercentual()));
    }

    @Test
    @DisplayName("Deve delegar faturamento por servicos para use case dedicado")
    void buscarFaturamentoServicos_delegacaoUseCase() {
        List<FaturamentoServicoResponse> esperado = List
                .of(FaturamentoServicoResponse.builder().categoria("Estetica Premium").servicos(List.of()).build());
        when(buscarFaturamentoServicosMensalUseCase.execute())
                .thenReturn(List.of(new FaturamentoServicoCategoriaResumo("Estetica Premium", List.of())));

        List<FaturamentoServicoResponse> result = dashboardService.buscarFaturamentoServicos();

        assertEquals(esperado.size(), result.size());
        assertEquals(esperado.getFirst().getCategoria(), result.getFirst().getCategoria());
    }

    @Test
    @DisplayName("Deve delegar faturamento por periodo para use case dedicado")
    void buscarFaturamentoPeriodo_delegacaoUseCase() {
        List<FaturamentoPeriodoResponse> esperado = List
                .of(FaturamentoPeriodoResponse.builder().faturamentoDiario(BigDecimal.ZERO).build());
        when(buscarFaturamentoPeriodoUseCase.execute())
                .thenReturn(List.of(new FaturamentoPeriodoResumo(null, BigDecimal.ZERO)));

        List<FaturamentoPeriodoResponse> result = dashboardService.buscarFaturamentoPeriodo();

        assertEquals(esperado.size(), result.size());
        assertEquals(0, esperado.getFirst().getFaturamentoDiario().compareTo(result.getFirst().getFaturamentoDiario()));
    }

    @Test
    @DisplayName("Deve delegar fluxo de caixa mensal para use case dedicado")
    void buscarFluxoCaixa_delegacaoUseCase() {
        FluxoCaixaResponse esperado = FluxoCaixaResponse.builder().total(BigDecimal.valueOf(1000))
                .lucro(BigDecimal.valueOf(700)).custo(BigDecimal.valueOf(300)).percentualLucro(new BigDecimal("70.00"))
                .percentualCusto(new BigDecimal("30.00")).build();
        when(buscarFluxoCaixaMensalUseCase.execute())
                .thenReturn(new FluxoCaixaResumo(esperado.getTotal(), esperado.getLucro(), esperado.getCusto(),
                        esperado.getPercentualLucro(), esperado.getPercentualCusto()));

        FluxoCaixaResponse result = dashboardService.buscarFluxoCaixa();

        assertEquals(0, esperado.getTotal().compareTo(result.getTotal()));
        assertEquals(0, esperado.getLucro().compareTo(result.getLucro()));
        assertEquals(0, esperado.getCusto().compareTo(result.getCusto()));
        assertEquals(0, esperado.getPercentualLucro().compareTo(result.getPercentualLucro()));
        assertEquals(0, esperado.getPercentualCusto().compareTo(result.getPercentualCusto()));
    }

    @Test
    @DisplayName("Deve delegar cancelamentos mensais para use case dedicado")
    void buscarCancelamentos_delegacaoUseCase() {
        List<CancelamentoResponse> esperado = List.of(
                CancelamentoResponse.builder().tipo("falta_peca").quantidade(5L).build(),
                CancelamentoResponse.builder().tipo("cliente_desistiu").quantidade(3L).build());
        when(buscarCancelamentosMensaisUseCase.execute()).thenReturn(
                List.of(new CancelamentoResumo("falta_peca", 5L), new CancelamentoResumo("cliente_desistiu", 3L)));

        List<CancelamentoResponse> result = dashboardService.buscarCancelamentos();

        assertEquals(esperado.size(), result.size());
        assertEquals(esperado.getFirst().getTipo(), result.getFirst().getTipo());
        assertEquals(esperado.getFirst().getQuantidade(), result.getFirst().getQuantidade());
        assertEquals(esperado.get(1).getTipo(), result.get(1).getTipo());
        assertEquals(esperado.get(1).getQuantidade(), result.get(1).getQuantidade());
    }

    @Test
    @DisplayName("Deve calcular ticket médio por OS concluída com variação percentual")
    void buscarTicketMedioMes_sucesso() {
        TicketMedioMensalResponse esperado = TicketMedioMensalResponse.builder()
                .totalTicketMedioMesAtual(BigDecimal.valueOf(300)).variacaoPercentual(new BigDecimal("50.0000"))
                .build();
        when(buscarTicketMedioMensalUseCase.execute()).thenReturn(
                new TicketMedioMensalResumo(esperado.getTotalTicketMedioMesAtual(), esperado.getVariacaoPercentual()));

        var result = dashboardService.buscarTicketMedioMes();

        assertEquals(0, esperado.getTotalTicketMedioMesAtual().compareTo(result.getTotalTicketMedioMesAtual()));
        assertEquals(0, esperado.getVariacaoPercentual().compareTo(result.getVariacaoPercentual()));
    }

    @Test
    @DisplayName("Deve retornar resumo da home com próximo agendamento")
    void buscarHomeResumo_comProximoAgendamento() {
        when(dashboardGateway.contarAgendamentosNoPeriodoExcetoStatus(any(), any(), any())).thenReturn(5L);
        when(dashboardGateway.somarFaturamentoEstimadoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(new BigDecimal("480.00"));
        when(dashboardGateway.buscarProximoAgendamentoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(java.util.Optional.of(new ProximoAgendamentoResumo(123L, "Higienização Interna",
                        LocalDateTime.of(2026, 3, 15, 14, 30), "Guilherme Serafim", "VW", "Golf", "ABC1D23", 2L)));

        HomeResumoResponse result = dashboardService.buscarHomeResumo();

        assertEquals(5L, result.getAgendamentosHoje());
        assertEquals(0, new BigDecimal("480.00").compareTo(result.getFaturamentoEstimadoHoje()));
        assertEquals(0, new BigDecimal("96.00").compareTo(result.getTicketMedioEstimadoHoje()));
        assertNotNull(result.getProximoAgendamento());
        assertEquals(123L, result.getProximoAgendamento().getOrdemServicoId());
        assertEquals("Higienização Interna", result.getProximoAgendamento().getServico());
        assertEquals("14:30", result.getProximoAgendamento().getHora());
        assertEquals("2026-03-15", result.getProximoAgendamento().getData());
        assertEquals("Guilherme Serafim", result.getProximoAgendamento().getClienteNome());
        assertEquals("VW Golf", result.getProximoAgendamento().getVeiculoDescricao());
        assertEquals(2L, result.getProximoAgendamento().getStatus());
    }

    @Test
    @DisplayName("Deve retornar próximo agendamento nulo quando não houver horário futuro no dia")
    void buscarHomeResumo_semProximoAgendamento() {
        when(dashboardGateway.contarAgendamentosNoPeriodoExcetoStatus(any(), any(), any())).thenReturn(2L);
        when(dashboardGateway.somarFaturamentoEstimadoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(new BigDecimal("200.00"));
        when(dashboardGateway.buscarProximoAgendamentoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(java.util.Optional.empty());

        HomeResumoResponse result = dashboardService.buscarHomeResumo();

        assertEquals(2L, result.getAgendamentosHoje());
        assertEquals(0, new BigDecimal("200.00").compareTo(result.getFaturamentoEstimadoHoje()));
        assertEquals(0, new BigDecimal("100.00").compareTo(result.getTicketMedioEstimadoHoje()));
        assertNull(result.getProximoAgendamento());
    }

    @Test
    @DisplayName("Deve retornar zeros e próximo nulo quando não houver agendamentos")
    void buscarHomeResumo_semAgendamentos() {
        when(dashboardGateway.contarAgendamentosNoPeriodoExcetoStatus(any(), any(), any())).thenReturn(0L);
        when(dashboardGateway.somarFaturamentoEstimadoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(dashboardGateway.buscarProximoAgendamentoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(java.util.Optional.empty());

        HomeResumoResponse result = dashboardService.buscarHomeResumo();

        assertEquals(0L, result.getAgendamentosHoje());
        assertEquals(0, new BigDecimal("0.00").compareTo(result.getFaturamentoEstimadoHoje()));
        assertEquals(0, new BigDecimal("0.00").compareTo(result.getTicketMedioEstimadoHoje()));
        assertNull(result.getProximoAgendamento());
    }

    @Test
    @DisplayName("Deve usar placa na descrição do veículo quando marca e modelo estiverem ausentes")
    void buscarHomeResumo_fallbackDescricaoVeiculo() {
        when(dashboardGateway.contarAgendamentosNoPeriodoExcetoStatus(any(), any(), any())).thenReturn(1L);
        when(dashboardGateway.somarFaturamentoEstimadoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(new BigDecimal("80.00"));
        when(dashboardGateway.buscarProximoAgendamentoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(java.util.Optional.of(new ProximoAgendamentoResumo(321L, "",
                        LocalDateTime.of(2026, 3, 15, 16, 0), "Cliente", null, null, "XYZ9Y88", 1L)));

        HomeResumoResponse result = dashboardService.buscarHomeResumo();

        assertNotNull(result.getProximoAgendamento());
        assertEquals("XYZ9Y88", result.getProximoAgendamento().getVeiculoDescricao());
        assertEquals("", result.getProximoAgendamento().getServico());
    }
}
