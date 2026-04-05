package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.domain.entity.CancelamentoMotivoResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoDiarioResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoResumo;
import com.automotiva.estetica.rick.domain.entity.ProximoAgendamentoResumo;
import com.automotiva.estetica.rick.infrastructure.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.PessoaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.StatusEntity;
import com.automotiva.estetica.rick.infrastructure.entity.VeiculoEntity;
import com.automotiva.estetica.rick.infrastructure.repository.ordemservico.OrdemServicoRepository;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DashboardGatewayImplTest {

    private OrdemServicoRepository repository;
    private DashboardGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        repository = mock(OrdemServicoRepository.class);
        gateway = new DashboardGatewayImpl(repository);
    }

    @Test
    void buscarCancelamentosPorMotivoDoPeriodo_deveMapearQuantidadeNulaComoZero() {
        when(repository.buscarCancelamentosPorMotivoDoPeriodo(any(), any()))
                .thenReturn(java.util.Collections.singletonList(new Object[] {"Cliente", null}));

        List<CancelamentoMotivoResumo> resultado = gateway.buscarCancelamentosPorMotivoDoPeriodo(
                LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals(1, resultado.size());
        assertEquals("Cliente", resultado.getFirst().tipo());
        assertEquals(0L, resultado.getFirst().quantidade());
    }

    @Test
    void buscarCancelamentosPorMotivoDoPeriodo_deveMapearQuantidadeQuandoNaoNula() {
        when(repository.buscarCancelamentosPorMotivoDoPeriodo(any(), any()))
                .thenReturn(java.util.Collections.singletonList(new Object[] {"Operacional", 3L}));

        List<CancelamentoMotivoResumo> resultado = gateway.buscarCancelamentosPorMotivoDoPeriodo(
                LocalDateTime.now().minusDays(2), LocalDateTime.now());

        assertEquals(1, resultado.size());
        assertEquals("Operacional", resultado.getFirst().tipo());
        assertEquals(3L, resultado.getFirst().quantidade());
    }

    @Test
    void buscarFaturamentoServicos_deveAplicarFallbacksDeNull() {
        when(repository.buscarFaturamentoServicos(any(), any()))
                .thenReturn(java.util.Collections.singletonList(new Object[] {null, "Servico X", null, "Categoria",
                        null, BigDecimal.TEN}));

        List<FaturamentoServicoResumo> resultado = gateway.buscarFaturamentoServicos(
                LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals(1, resultado.size());
        FaturamentoServicoResumo item = resultado.getFirst();
        assertNull(item.servicoId());
        assertNull(item.categoriaId());
        assertEquals(0L, item.quantidadeVendida());
        assertEquals(BigDecimal.TEN, item.faturamento());
    }

    @Test
    void buscarFaturamentoServicos_deveMapearCamposQuandoNaoNulos() {
        when(repository.buscarFaturamentoServicos(any(), any())).thenReturn(java.util.Collections.singletonList(
                new Object[] {1L, "Servico Y", 2L, "Categoria Y", 4L, BigDecimal.valueOf(40)}));

        List<FaturamentoServicoResumo> resultado = gateway.buscarFaturamentoServicos(
                LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals(1, resultado.size());
        FaturamentoServicoResumo item = resultado.getFirst();
        assertEquals(1L, item.servicoId());
        assertEquals(2L, item.categoriaId());
        assertEquals(4L, item.quantidadeVendida());
        assertEquals(BigDecimal.valueOf(40), item.faturamento());
    }

    @Test
    void buscarProximoAgendamentoNoPeriodoExcetoStatus_deveRetornarVazioQuandoNaoEncontrar() {
        when(repository.findFirstByDataAgendamentoBetweenAndStatus_IdNotOrderByDataAgendamentoAscIdAsc(any(), any(),
                any())).thenReturn(Optional.empty());

        Optional<ProximoAgendamentoResumo> resultado = gateway.buscarProximoAgendamentoNoPeriodoExcetoStatus(
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), 4L);

        assertTrue(resultado.isEmpty());
        verify(repository, never()).buscarNomesServicosDaOrdem(anyLong());
    }

    @Test
    void buscarProximoAgendamentoNoPeriodoExcetoStatus_deveMapearCamposNulosComFallback() {
        OrdemServicoEntity ordem = OrdemServicoEntity.builder().id(10L).dataAgendamento(LocalDateTime.now()).build();

        when(repository.findFirstByDataAgendamentoBetweenAndStatus_IdNotOrderByDataAgendamentoAscIdAsc(any(), any(),
                any())).thenReturn(Optional.of(ordem));
        when(repository.buscarNomesServicosDaOrdem(10L)).thenReturn(List.of());

        ProximoAgendamentoResumo resultado = gateway.buscarProximoAgendamentoNoPeriodoExcetoStatus(
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), 4L).orElseThrow();

        assertEquals("", resultado.servico());
        assertEquals("", resultado.clienteNome());
        assertNull(resultado.veiculoMarca());
        assertNull(resultado.veiculoModelo());
        assertNull(resultado.veiculoPlaca());
        assertNull(resultado.status());
    }

    @Test
    void buscarProximoAgendamentoNoPeriodoExcetoStatus_deveUsarPrimeiroServicoEMapearDados() {
        PessoaEntity pessoa = PessoaEntity.builder().nome("Rodrigo").build();
        VeiculoEntity veiculo = VeiculoEntity.builder().marca("VW").modelo("Gol").placa("ABC1234").pessoa(pessoa)
                .build();
        StatusEntity status = StatusEntity.builder().id(2L).descricao("Analise").build();
        OrdemServicoEntity ordem = OrdemServicoEntity.builder().id(20L).veiculo(veiculo).status(status)
                .dataAgendamento(LocalDateTime.now()).build();

        when(repository.findFirstByDataAgendamentoBetweenAndStatus_IdNotOrderByDataAgendamentoAscIdAsc(any(), any(),
                any())).thenReturn(Optional.of(ordem));
        when(repository.buscarNomesServicosDaOrdem(20L)).thenReturn(List.of("Polimento", "Vitrificacao"));

        ProximoAgendamentoResumo resultado = gateway.buscarProximoAgendamentoNoPeriodoExcetoStatus(
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), 4L).orElseThrow();

        assertEquals("Polimento", resultado.servico());
        assertEquals("Rodrigo", resultado.clienteNome());
        assertEquals("VW", resultado.veiculoMarca());
        assertEquals("Gol", resultado.veiculoModelo());
        assertEquals("ABC1234", resultado.veiculoPlaca());
        assertEquals(2L, resultado.status());
    }

    @Test
    void buscarProximoAgendamentoNoPeriodoExcetoStatus_deveMapearClienteVazioQuandoPessoaNula() {
        VeiculoEntity veiculo = VeiculoEntity.builder().marca("Ford").modelo("Ka").placa("XYZ0001").pessoa(null)
                .build();
        OrdemServicoEntity ordem = OrdemServicoEntity.builder().id(30L).veiculo(veiculo)
                .dataAgendamento(LocalDateTime.now()).build();

        when(repository.findFirstByDataAgendamentoBetweenAndStatus_IdNotOrderByDataAgendamentoAscIdAsc(any(), any(),
                any())).thenReturn(Optional.of(ordem));
        when(repository.buscarNomesServicosDaOrdem(30L)).thenReturn(List.of("Lavagem"));

        ProximoAgendamentoResumo resultado = gateway.buscarProximoAgendamentoNoPeriodoExcetoStatus(
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), 4L).orElseThrow();

        assertEquals("", resultado.clienteNome());
        assertEquals("Ford", resultado.veiculoMarca());
        assertEquals("Ka", resultado.veiculoModelo());
        assertEquals("XYZ0001", resultado.veiculoPlaca());
    }

    @Test
    void buscarFaturamentoPorDia_deveMapearDataSqlParaLocalDate() {
        when(repository.buscarFaturamentoPorDia(any())).thenReturn(
                java.util.Collections.singletonList(new Object[] {Date.valueOf("2026-04-03"), BigDecimal.valueOf(250)}));

        List<FaturamentoDiarioResumo> resultado = gateway.buscarFaturamentoPorDia(LocalDateTime.now().minusDays(5));

        assertEquals(1, resultado.size());
        assertEquals(java.time.LocalDate.of(2026, 4, 3), resultado.getFirst().dia());
        assertEquals(BigDecimal.valueOf(250), resultado.getFirst().totalDia());
    }

    @Test
    void metodosDeSomaEContagem_deveDelegarRepositorio() {
        when(repository.somarFaturamentoDoPeriodo(any(), any())).thenReturn(BigDecimal.valueOf(1000));
        when(repository.buscarQtdOrdensDoMes(any(), any())).thenReturn(12);
        when(repository.buscarQtdOrdensConcluidasNoMes(any(), any())).thenReturn(7);
        when(repository.calcularTicketMedioDoMes(any(), any())).thenReturn(BigDecimal.valueOf(85));
        when(repository.somarReceitaRecebidaDoPeriodo(any(), any())).thenReturn(BigDecimal.valueOf(900));
        when(repository.somarCustoRealizadoDoPeriodo(any(), any())).thenReturn(BigDecimal.valueOf(300));
        when(repository.contarAgendamentosNoPeriodoExcetoStatus(any(), any(), any())).thenReturn(4L);
        when(repository.somarFaturamentoEstimadoNoPeriodoExcetoStatus(any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(1000),
                gateway.somarFaturamentoDoPeriodo(LocalDateTime.now().minusDays(1), LocalDateTime.now()));
        assertEquals(12, gateway.buscarQtdOrdensDoMes(LocalDateTime.now().minusDays(1), LocalDateTime.now()));
        assertEquals(7,
                gateway.buscarQtdOrdensConcluidasNoMes(LocalDateTime.now().minusDays(1), LocalDateTime.now()));
        assertEquals(BigDecimal.valueOf(85),
                gateway.calcularTicketMedioDoMes(LocalDateTime.now().minusDays(1), LocalDateTime.now()));
        assertEquals(BigDecimal.valueOf(900),
                gateway.somarReceitaRecebidaDoPeriodo(LocalDateTime.now().minusDays(1), LocalDateTime.now()));
        assertEquals(BigDecimal.valueOf(300),
                gateway.somarCustoRealizadoDoPeriodo(LocalDateTime.now().minusDays(1), LocalDateTime.now()));
        assertEquals(4L, gateway.contarAgendamentosNoPeriodoExcetoStatus(LocalDateTime.now().minusDays(1),
                LocalDateTime.now(), 2L));
        assertEquals(BigDecimal.valueOf(500), gateway.somarFaturamentoEstimadoNoPeriodoExcetoStatus(
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), 2L));
    }
}

