package com.automotiva.estetica.rick.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.response.CancelamentoMotivoDto;
import com.automotiva.estetica.rick.application.dto.response.CancelamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoDiarioDto;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoDto;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoItemResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoResponse;
import com.automotiva.estetica.rick.application.dto.response.FluxoCaixaResponse;
import com.automotiva.estetica.rick.application.dto.response.HomeResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.ProximoAgendamentoDto;
import com.automotiva.estetica.rick.application.port.out.OrdemServicoRepositoryPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks private DashboardService dashboardService;

    @Mock private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

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

    @Test
    @DisplayName("Deve retornar faturamento agrupado por categoria no mês atual")
    void buscarFaturamentoServicos_sucesso() {
        when(ordemServicoRepositoryPort.buscarFaturamentoServicos(any(), any()))
                .thenReturn(
                        List.of(
                                new FaturamentoServicoDto(
                                        1L,
                                        "Vitrificacao",
                                        10L,
                                        "Estetica Premium",
                                        2L,
                                        BigDecimal.valueOf(500)),
                                new FaturamentoServicoDto(
                                        2L,
                                        "Polimento",
                                        10L,
                                        "Estetica Premium",
                                        1L,
                                        BigDecimal.valueOf(200))));

        List<FaturamentoServicoResponse> result = dashboardService.buscarFaturamentoServicos();

        assertEquals(1, result.size());
        assertEquals("Estetica Premium", result.get(0).getCategoria());
        assertNotNull(result.get(0).getServicos());
        assertEquals(2, result.get(0).getServicos().size());

        FaturamentoServicoItemResponse primeiroServico = result.get(0).getServicos().get(0);
        assertEquals("Vitrificacao", primeiroServico.getServico());
        assertEquals(2L, primeiroServico.getQuantidadeVendida());
        assertEquals(0, BigDecimal.valueOf(500).compareTo(primeiroServico.getFaturamento()));
    }

    @Test
    @DisplayName("Deve retornar faturamento diário com campos data e faturamentoDiario")
    void buscarFaturamentoPeriodo_sucesso() {
        when(ordemServicoRepositoryPort.buscarFaturamentoPorDia(any()))
                .thenReturn(List.of(new FaturamentoDiarioDto(LocalDate.of(2026, 3, 1), null)));

        var result = dashboardService.buscarFaturamentoPeriodo();

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2026, 3, 1), result.getFirst().getData());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getFirst().getFaturamentoDiario()));
    }

    @Test
    @DisplayName("Deve retornar fluxo de caixa zerado quando não houver dados")
    void buscarFluxoCaixa_semDados() {
        when(ordemServicoRepositoryPort.somarReceitaRecebidaDoPeriodo(any(), any())).thenReturn(null);
        when(ordemServicoRepositoryPort.somarCustoRealizadoDoPeriodo(any(), any())).thenReturn(null);

        FluxoCaixaResponse result = dashboardService.buscarFluxoCaixa();

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getLucro()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getCusto()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPercentualLucro()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPercentualCusto()));
    }

    @Test
    @DisplayName("Deve retornar 100 por cento de lucro quando só existir receita")
    void buscarFluxoCaixa_somenteLucro() {
        when(ordemServicoRepositoryPort.somarReceitaRecebidaDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.valueOf(1200));
        when(ordemServicoRepositoryPort.somarCustoRealizadoDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.ZERO);

        FluxoCaixaResponse result = dashboardService.buscarFluxoCaixa();

        assertEquals(0, BigDecimal.valueOf(1200).compareTo(result.getTotal()));
        assertEquals(0, BigDecimal.valueOf(1200).compareTo(result.getLucro()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getCusto()));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(result.getPercentualLucro()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPercentualCusto()));
    }

    @Test
    @DisplayName("Deve retornar 100 por cento de custo quando só existir custo")
    void buscarFluxoCaixa_somenteCusto() {
        when(ordemServicoRepositoryPort.somarReceitaRecebidaDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(ordemServicoRepositoryPort.somarCustoRealizadoDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.valueOf(850));

        FluxoCaixaResponse result = dashboardService.buscarFluxoCaixa();

        assertEquals(0, BigDecimal.valueOf(850).compareTo(result.getTotal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getLucro()));
        assertEquals(0, BigDecimal.valueOf(850).compareTo(result.getCusto()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPercentualLucro()));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(result.getPercentualCusto()));
    }

    @Test
    @DisplayName("Deve calcular fluxo de caixa com lucro e custo no periodo")
    void buscarFluxoCaixa_lucroECusto() {
        when(ordemServicoRepositoryPort.somarReceitaRecebidaDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.valueOf(12000));
        when(ordemServicoRepositoryPort.somarCustoRealizadoDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.valueOf(4500));

        FluxoCaixaResponse result = dashboardService.buscarFluxoCaixa();

        assertEquals(0, BigDecimal.valueOf(16500).compareTo(result.getTotal()));
        assertEquals(0, BigDecimal.valueOf(12000).compareTo(result.getLucro()));
        assertEquals(0, BigDecimal.valueOf(4500).compareTo(result.getCusto()));
        assertEquals(0, new BigDecimal("72.73").compareTo(result.getPercentualLucro()));
        assertEquals(0, new BigDecimal("27.27").compareTo(result.getPercentualCusto()));
    }

    @Test
    @DisplayName("Deve arredondar percentuais do fluxo de caixa com duas casas")
    void buscarFluxoCaixa_arredondamentoPercentuais() {
        when(ordemServicoRepositoryPort.somarReceitaRecebidaDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.ONE);
        when(ordemServicoRepositoryPort.somarCustoRealizadoDoPeriodo(any(), any()))
                .thenReturn(BigDecimal.valueOf(2));

        FluxoCaixaResponse result = dashboardService.buscarFluxoCaixa();

        assertEquals(0, new BigDecimal("33.33").compareTo(result.getPercentualLucro()));
        assertEquals(0, new BigDecimal("66.67").compareTo(result.getPercentualCusto()));
    }

    @Test
    @DisplayName("Deve retornar cancelamentos agrupados por tipo em ordem desc")
    void buscarCancelamentos_multiplosTipos() {
        when(ordemServicoRepositoryPort.buscarCancelamentosPorMotivoDoPeriodo(any(), any()))
                .thenReturn(
                        List.of(
                                new CancelamentoMotivoDto("Cliente desistiu", 3L),
                                new CancelamentoMotivoDto("Falta peca", 5L),
                                new CancelamentoMotivoDto("Agenda conflito", 2L)));

        List<CancelamentoResponse> result = dashboardService.buscarCancelamentos();

        assertEquals(3, result.size());
        assertEquals("falta_peca", result.get(0).getTipo());
        assertEquals(5L, result.get(0).getQuantidade());
        assertEquals("cliente_desistiu", result.get(1).getTipo());
        assertEquals(3L, result.get(1).getQuantidade());
        assertEquals("agenda_conflito", result.get(2).getTipo());
    }

    @Test
    @DisplayName("Deve retornar um unico tipo de cancelamento")
    void buscarCancelamentos_tipoUnico() {
        when(ordemServicoRepositoryPort.buscarCancelamentosPorMotivoDoPeriodo(any(), any()))
                .thenReturn(List.of(new CancelamentoMotivoDto("Problema pagamento", 4L)));

        List<CancelamentoResponse> result = dashboardService.buscarCancelamentos();

        assertEquals(1, result.size());
        assertEquals("problema_pagamento", result.getFirst().getTipo());
        assertEquals(4L, result.getFirst().getQuantidade());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nao houver cancelamentos")
    void buscarCancelamentos_semDados() {
        when(ordemServicoRepositoryPort.buscarCancelamentosPorMotivoDoPeriodo(any(), any()))
                .thenReturn(List.of());

        List<CancelamentoResponse> result = dashboardService.buscarCancelamentos();

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Deve usar nao_informado para motivo nulo ou vazio")
    void buscarCancelamentos_motivoNuloOuVazio() {
        when(ordemServicoRepositoryPort.buscarCancelamentosPorMotivoDoPeriodo(any(), any()))
                .thenReturn(
                        List.of(
                                new CancelamentoMotivoDto(null, 2L),
                                new CancelamentoMotivoDto(" ", 3L)));

        List<CancelamentoResponse> result = dashboardService.buscarCancelamentos();

        assertEquals(1, result.size());
        assertEquals("nao_informado", result.getFirst().getTipo());
        assertEquals(5L, result.getFirst().getQuantidade());
    }

    @Test
    @DisplayName("Deve calcular ticket médio por OS concluída com variação percentual")
    void buscarTicketMedioMes_sucesso() {
        when(ordemServicoRepositoryPort.calcularTicketMedioDoMes(any(), any()))
                .thenReturn(BigDecimal.valueOf(300), BigDecimal.valueOf(200));

        var result = dashboardService.buscarTicketMedioMes();

        assertEquals(0, BigDecimal.valueOf(300).compareTo(result.getTotalTicketMedioMesAtual()));
        assertEquals(0, new BigDecimal("50.0000").compareTo(result.getVariacaoPercentual()));
    }

    @Test
    @DisplayName("Deve retornar resumo da home com próximo agendamento")
    void buscarHomeResumo_comProximoAgendamento() {
        when(ordemServicoRepositoryPort.contarAgendamentosNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(5L);
        when(ordemServicoRepositoryPort.somarFaturamentoEstimadoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(new BigDecimal("480.00"));
        when(ordemServicoRepositoryPort.buscarProximoAgendamentoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(
                        java.util.Optional.of(
                                new ProximoAgendamentoDto(
                                        123L,
                                        "Higienização Interna",
                                        LocalDateTime.of(2026, 3, 15, 14, 30),
                                        "Guilherme Serafim",
                                        "VW",
                                        "Golf",
                                        "ABC1D23",
                                        2L)));

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
        when(ordemServicoRepositoryPort.contarAgendamentosNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(2L);
        when(ordemServicoRepositoryPort.somarFaturamentoEstimadoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(new BigDecimal("200.00"));
        when(ordemServicoRepositoryPort.buscarProximoAgendamentoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(java.util.Optional.empty());

        HomeResumoResponse result = dashboardService.buscarHomeResumo();

        assertEquals(2L, result.getAgendamentosHoje());
        assertEquals(0, new BigDecimal("200.00").compareTo(result.getFaturamentoEstimadoHoje()));
        assertEquals(0, new BigDecimal("100.00").compareTo(result.getTicketMedioEstimadoHoje()));
        assertEquals(null, result.getProximoAgendamento());
    }

    @Test
    @DisplayName("Deve retornar zeros e próximo nulo quando não houver agendamentos")
    void buscarHomeResumo_semAgendamentos() {
        when(ordemServicoRepositoryPort.contarAgendamentosNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(0L);
        when(ordemServicoRepositoryPort.somarFaturamentoEstimadoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(ordemServicoRepositoryPort.buscarProximoAgendamentoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(java.util.Optional.empty());

        HomeResumoResponse result = dashboardService.buscarHomeResumo();

        assertEquals(0L, result.getAgendamentosHoje());
        assertEquals(0, new BigDecimal("0.00").compareTo(result.getFaturamentoEstimadoHoje()));
        assertEquals(0, new BigDecimal("0.00").compareTo(result.getTicketMedioEstimadoHoje()));
        assertEquals(null, result.getProximoAgendamento());
    }

    @Test
    @DisplayName("Deve usar placa na descrição do veículo quando marca e modelo estiverem ausentes")
    void buscarHomeResumo_fallbackDescricaoVeiculo() {
        when(ordemServicoRepositoryPort.contarAgendamentosNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(1L);
        when(ordemServicoRepositoryPort.somarFaturamentoEstimadoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(new BigDecimal("80.00"));
        when(ordemServicoRepositoryPort.buscarProximoAgendamentoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(
                        java.util.Optional.of(
                                new ProximoAgendamentoDto(
                                        321L,
                                        "",
                                        LocalDateTime.of(2026, 3, 15, 16, 0),
                                        "Cliente",
                                        null,
                                        null,
                                        "XYZ9Y88",
                                        1L)));

        HomeResumoResponse result = dashboardService.buscarHomeResumo();

        assertNotNull(result.getProximoAgendamento());
        assertEquals("XYZ9Y88", result.getProximoAgendamento().getVeiculoDescricao());
        assertEquals("", result.getProximoAgendamento().getServico());
    }
}
