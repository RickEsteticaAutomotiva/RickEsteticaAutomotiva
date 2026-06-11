package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.CancelamentoMotivoResumo;
import com.automotiva.estetica.rick.domain.entity.CancelamentoResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoDiarioResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoMensalResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoPeriodoResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoCategoriaResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoResumo;
import com.automotiva.estetica.rick.domain.entity.FluxoCaixaResumo;
import com.automotiva.estetica.rick.domain.entity.QtdOrdensConcluidasMensalResumo;
import com.automotiva.estetica.rick.domain.entity.QtdOrdensMensalResumo;
import com.automotiva.estetica.rick.domain.entity.TicketMedioMensalResumo;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardUseCasesTest {

    @Mock
    private DashboardGateway dashboardGateway;

    @InjectMocks
    private BuscarFaturamentoMensalUseCase buscarFaturamentoMensalUseCase;

    @InjectMocks
    private BuscarQtdOrdensMensaisUseCase buscarQtdOrdensMensaisUseCase;

    @InjectMocks
    private BuscarQtdOrdensConcluidasMensaisUseCase buscarQtdOrdensConcluidasMensaisUseCase;

    @InjectMocks
    private BuscarTicketMedioMensalUseCase buscarTicketMedioMensalUseCase;

    @InjectMocks
    private BuscarFluxoCaixaMensalUseCase buscarFluxoCaixaMensalUseCase;

    @InjectMocks
    private BuscarCancelamentosMensaisUseCase buscarCancelamentosMensaisUseCase;

    @InjectMocks
    private BuscarFaturamentoPeriodoUseCase buscarFaturamentoPeriodoUseCase;

    @InjectMocks
    private BuscarFaturamentoServicosMensalUseCase buscarFaturamentoServicosMensalUseCase;

    @Test
    @DisplayName("Faturamento mensal: deve calcular variacao positiva")
    void buscarFaturamentoMensal_variacaoPositiva() {
        when(dashboardGateway.somarFaturamentoDoPeriodo(any(), any())).thenReturn(BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500));

        FaturamentoMensalResumo result = buscarFaturamentoMensalUseCase.execute();

        assertEquals(0, BigDecimal.valueOf(1000).compareTo(result.faturamentoAtual()));
        assertEquals(0, new BigDecimal("100.0000").compareTo(result.variacaoPercentual()));
    }

    @Test
    @DisplayName("Faturamento mensal: deve retornar variacao zero quando anterior for zero")
    void buscarFaturamentoMensal_anteriorZero() {
        when(dashboardGateway.somarFaturamentoDoPeriodo(any(), any())).thenReturn(BigDecimal.valueOf(1000),
                BigDecimal.ZERO);

        FaturamentoMensalResumo result = buscarFaturamentoMensalUseCase.execute();

        assertEquals(BigDecimal.ZERO, result.variacaoPercentual());
    }

    @Test
    @DisplayName("Qtd ordens mensal: deve calcular variacao positiva")
    void buscarQtdOrdensMensais_variacaoPositiva() {
        when(dashboardGateway.buscarQtdOrdensDoMes(any(), any())).thenReturn(15, 10);

        QtdOrdensMensalResumo result = buscarQtdOrdensMensaisUseCase.execute();

        assertEquals(15, result.totalOrdens());
        assertEquals(0, new BigDecimal("50.0000").compareTo(result.variacaoPercentual()));
    }

    @Test
    @DisplayName("Qtd ordens mensal: deve usar o mês atual inteiro no período da consulta")
    void buscarQtdOrdensMensais_deveUsarMesAtualInteiro() {
        when(dashboardGateway.buscarQtdOrdensDoMes(any(), any())).thenReturn(15, 10);

        LocalDate hoje = LocalDate.now();

        buscarQtdOrdensMensaisUseCase.execute();

        ArgumentCaptor<LocalDateTime> inicioCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> fimCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        org.mockito.Mockito.verify(dashboardGateway, org.mockito.Mockito.times(2))
                .buscarQtdOrdensDoMes(inicioCaptor.capture(), fimCaptor.capture());

        assertEquals(hoje.withDayOfMonth(1).atStartOfDay(), inicioCaptor.getAllValues().get(0));
        assertEquals(hoje.withDayOfMonth(hoje.lengthOfMonth()).atTime(23, 59, 59), fimCaptor.getAllValues().get(0));
    }

    @Test
    @DisplayName("Qtd ordens mensal: deve retornar variacao zero quando periodo anterior for zero")
    void buscarQtdOrdensMensais_anteriorZero() {
        when(dashboardGateway.buscarQtdOrdensDoMes(any(), any())).thenReturn(5, 0);

        QtdOrdensMensalResumo result = buscarQtdOrdensMensaisUseCase.execute();

        assertEquals(BigDecimal.ZERO, result.variacaoPercentual());
    }

    @Test
    @DisplayName("Qtd ordens concluidas mensal: deve calcular variacao positiva")
    void buscarQtdOrdensConcluidasMensais_variacaoPositiva() {
        when(dashboardGateway.buscarQtdOrdensConcluidasNoMes(any(), any())).thenReturn(8, 4);

        QtdOrdensConcluidasMensalResumo result = buscarQtdOrdensConcluidasMensaisUseCase.execute();

        assertEquals(8, result.totalOrdensConcluidas());
        assertEquals(0, new BigDecimal("100.0000").compareTo(result.variacaoPercentual()));
    }

    @Test
    @DisplayName("Qtd ordens concluidas mensal: deve retornar variacao zero quando anterior for zero")
    void buscarQtdOrdensConcluidasMensais_anteriorZero() {
        when(dashboardGateway.buscarQtdOrdensConcluidasNoMes(any(), any())).thenReturn(3, 0);

        QtdOrdensConcluidasMensalResumo result = buscarQtdOrdensConcluidasMensaisUseCase.execute();

        assertEquals(BigDecimal.ZERO, result.variacaoPercentual());
    }

    @Test
    @DisplayName("Ticket medio mensal: deve calcular variacao positiva")
    void buscarTicketMedioMensal_variacaoPositiva() {
        when(dashboardGateway.calcularTicketMedioDoMes(any(), any())).thenReturn(BigDecimal.valueOf(300),
                BigDecimal.valueOf(200));

        TicketMedioMensalResumo result = buscarTicketMedioMensalUseCase.execute();

        assertEquals(0, BigDecimal.valueOf(300).compareTo(result.totalTicketMedioMesAtual()));
        assertEquals(0, new BigDecimal("50.0000").compareTo(result.variacaoPercentual()));
    }

    @Test
    @DisplayName("Ticket medio mensal: deve retornar variacao zero quando anterior for zero")
    void buscarTicketMedioMensal_anteriorZero() {
        when(dashboardGateway.calcularTicketMedioDoMes(any(), any())).thenReturn(BigDecimal.valueOf(200),
                BigDecimal.ZERO);

        TicketMedioMensalResumo result = buscarTicketMedioMensalUseCase.execute();

        assertEquals(BigDecimal.ZERO, result.variacaoPercentual());
    }

    @Test
    @DisplayName("Fluxo caixa mensal: deve calcular totais e percentuais")
    void buscarFluxoCaixaMensal_sucesso() {
        when(dashboardGateway.somarReceitaRecebidaDoPeriodo(any(), any())).thenReturn(BigDecimal.valueOf(12000));
        when(dashboardGateway.somarCustoRealizadoDoPeriodo(any(), any())).thenReturn(BigDecimal.valueOf(4500));

        LocalDateTime antes = LocalDateTime.now();
        FluxoCaixaResumo result = buscarFluxoCaixaMensalUseCase.execute();
        LocalDateTime depois = LocalDateTime.now();

        ArgumentCaptor<LocalDateTime> inicioCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> fimCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        org.mockito.Mockito.verify(dashboardGateway).somarReceitaRecebidaDoPeriodo(inicioCaptor.capture(),
                fimCaptor.capture());
        org.mockito.Mockito.verify(dashboardGateway).somarCustoRealizadoDoPeriodo(inicioCaptor.getValue(),
                fimCaptor.getValue());

        assertEquals(0, BigDecimal.valueOf(16500).compareTo(result.total()));
        assertEquals(0, new BigDecimal("72.73").compareTo(result.percentualLucro()));
        assertEquals(0, new BigDecimal("27.27").compareTo(result.percentualCusto()));
        assertEquals(LocalDate.now().minusDays(30).atStartOfDay(), inicioCaptor.getValue());
        assertTrue(!fimCaptor.getValue().isBefore(antes) && !fimCaptor.getValue().isAfter(depois));
    }

    @Test
    @DisplayName("Fluxo caixa mensal: deve aplicar zero quando gateway retornar nulo")
    void buscarFluxoCaixaMensal_quandoValoresNulos_deveRetornarZeros() {
        when(dashboardGateway.somarReceitaRecebidaDoPeriodo(any(), any())).thenReturn(null);
        when(dashboardGateway.somarCustoRealizadoDoPeriodo(any(), any())).thenReturn(null);

        FluxoCaixaResumo result = buscarFluxoCaixaMensalUseCase.execute();

        assertEquals(0, BigDecimal.ZERO.compareTo(result.total()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.percentualLucro()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.percentualCusto()));
    }

    @Test
    @DisplayName("Cancelamentos mensais: deve agrupar e normalizar tipos")
    void buscarCancelamentosMensais_sucesso() {
        when(dashboardGateway.buscarCancelamentosPorMotivoDoPeriodo(any(), any())).thenReturn(List.of(
                new CancelamentoMotivoResumo("Cliente desistiu", 3L), new CancelamentoMotivoResumo("Falta peca", 5L),
                new CancelamentoMotivoResumo("Agenda conflito", 2L), new CancelamentoMotivoResumo(" ", 1L)));

        List<CancelamentoResumo> result = buscarCancelamentosMensaisUseCase.execute();

        assertEquals(4, result.size());
        assertEquals("falta_peca", result.getFirst().tipo());
        assertEquals(5L, result.getFirst().quantidade());
        assertEquals("nao_informado", result.getLast().tipo());
    }

    @Test
    @DisplayName("Cancelamentos mensais: deve somar motivos normalizados equivalentes")
    void buscarCancelamentosMensais_deveSomarMotivosEquivalentes() {
        when(dashboardGateway.buscarCancelamentosPorMotivoDoPeriodo(any(), any()))
                .thenReturn(List.of(new CancelamentoMotivoResumo("Falta peca", 2L),
                        new CancelamentoMotivoResumo("falta peça", 3L), new CancelamentoMotivoResumo("___", null)));

        List<CancelamentoResumo> result = buscarCancelamentosMensaisUseCase.execute();

        assertEquals(2, result.size());
        assertEquals("falta_peca", result.getFirst().tipo());
        assertEquals(5L, result.getFirst().quantidade());
        assertEquals("nao_informado", result.get(1).tipo());
        assertEquals(0L, result.get(1).quantidade());
    }

    @Test
    @DisplayName("Cancelamentos mensais: deve normalizar tipo nulo para nao_informado")
    void buscarCancelamentosMensais_quandoTipoNulo_deveNormalizar() {
        when(dashboardGateway.buscarCancelamentosPorMotivoDoPeriodo(any(), any()))
                .thenReturn(List.of(new CancelamentoMotivoResumo(null, 4L)));

        List<CancelamentoResumo> result = buscarCancelamentosMensaisUseCase.execute();

        assertEquals(1, result.size());
        assertEquals("nao_informado", result.getFirst().tipo());
        assertEquals(4L, result.getFirst().quantidade());
    }

    @Test
    @DisplayName("Faturamento periodo: deve mapear dia e preencher total nulo com zero")
    void buscarFaturamentoPeriodo_sucesso() {
        when(dashboardGateway.buscarFaturamentoPorDia(any()))
                .thenReturn(List.of(new FaturamentoDiarioResumo(LocalDate.of(2026, 3, 1), null)));

        List<FaturamentoPeriodoResumo> result = buscarFaturamentoPeriodoUseCase.execute();

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2026, 3, 1), result.getFirst().data());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getFirst().faturamentoDiario()));
    }

    @Test
    @DisplayName("Faturamento periodo: deve manter total quando valor vier preenchido")
    void buscarFaturamentoPeriodo_quandoValorPreenchido_deveManterValor() {
        when(dashboardGateway.buscarFaturamentoPorDia(any()))
                .thenReturn(List.of(new FaturamentoDiarioResumo(LocalDate.of(2026, 3, 2), BigDecimal.valueOf(321.45))));

        List<FaturamentoPeriodoResumo> result = buscarFaturamentoPeriodoUseCase.execute();

        assertEquals(1, result.size());
        assertEquals(0, BigDecimal.valueOf(321.45).compareTo(result.getFirst().faturamentoDiario()));
    }

    @Test
    @DisplayName("Faturamento servicos: deve agrupar por categoria e mapear campos")
    void buscarFaturamentoServicosMensal_sucesso() {
        when(dashboardGateway.buscarFaturamentoServicos(any(), any())).thenReturn(List.of(
                new FaturamentoServicoResumo(1L, "Vitrificacao", 10L, "Estetica Premium", 2L, BigDecimal.valueOf(500)),
                new FaturamentoServicoResumo(2L, "Polimento", 10L, "Estetica Premium", 1L, BigDecimal.valueOf(200))));

        List<FaturamentoServicoCategoriaResumo> result = buscarFaturamentoServicosMensalUseCase.execute();

        assertEquals(1, result.size());
        assertEquals("Estetica Premium", result.getFirst().categoria());
        assertEquals(2, result.getFirst().servicos().size());
        assertEquals("Vitrificacao", result.getFirst().servicos().getFirst().servico());
    }

    @Test
    @DisplayName("Faturamento servicos: deve aplicar defaults para campos nulos/blank")
    void buscarFaturamentoServicosMensal_deveAplicarDefaults() {
        when(dashboardGateway.buscarFaturamentoServicos(any(), any()))
                .thenReturn(List.of(new FaturamentoServicoResumo(1L, " ", 10L, "", null, null)));

        List<FaturamentoServicoCategoriaResumo> result = buscarFaturamentoServicosMensalUseCase.execute();

        assertEquals(1, result.size());
        assertEquals("Sem categoria", result.getFirst().categoria());
        assertEquals("Servico sem nome", result.getFirst().servicos().getFirst().servico());
        assertEquals(0L, result.getFirst().servicos().getFirst().quantidadeVendida());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getFirst().servicos().getFirst().faturamento()));
    }

    @Test
    @DisplayName("Faturamento servicos: deve aplicar defaults quando categoria e servico forem nulos")
    void buscarFaturamentoServicosMensal_quandoCategoriaEServicoNulos_deveAplicarDefaults() {
        when(dashboardGateway.buscarFaturamentoServicos(any(), any()))
                .thenReturn(List.of(new FaturamentoServicoResumo(3L, null, 99L, null, 1L, BigDecimal.TEN)));

        List<FaturamentoServicoCategoriaResumo> result = buscarFaturamentoServicosMensalUseCase.execute();

        assertEquals(1, result.size());
        assertEquals("Sem categoria", result.getFirst().categoria());
        assertEquals("Servico sem nome", result.getFirst().servicos().getFirst().servico());
    }

    @Test
    @DisplayName("Faturamento servicos: deve retornar vazio quando gateway não trouxer dados")
    void buscarFaturamentoServicosMensal_quandoVazio_deveRetornarListaVazia() {
        when(dashboardGateway.buscarFaturamentoServicos(any(), any())).thenReturn(List.of());

        List<FaturamentoServicoCategoriaResumo> result = buscarFaturamentoServicosMensalUseCase.execute();

        assertEquals(0, result.size());
    }
}
