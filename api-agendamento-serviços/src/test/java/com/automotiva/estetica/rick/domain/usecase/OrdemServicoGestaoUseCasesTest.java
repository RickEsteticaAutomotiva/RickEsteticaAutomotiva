package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.entity.Status;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.enums.StatusOrdem;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoEventGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de use cases de gestão de ordem")
class OrdemServicoGestaoUseCasesTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private OrdemServicoEventGateway ordemServicoEventGateway;

    @Mock
    private ItemServicoGateway itemServicoGateway;

    @Mock
    private ServicoGateway servicoGateway;

    private OrdemServico ordemBase() {
        return OrdemServico.builder().id(10L).dataAgendamento(LocalDateTime.now())
                .veiculo(Veiculo.builder().id(1L).build())
                .status(Status.builder().id(StatusOrdem.AGUARDANDO.getId()).build()).build();
    }

    @Test
    @DisplayName("AdicionarServicosOrdemServicoUseCase deve lançar quando lista de serviços for nula")
    void adicionarServicos_deveLancarQuandoListaNula() {
        AdicionarServicosOrdemServicoUseCase useCase = new AdicionarServicosOrdemServicoUseCase(ordemServicoGateway,
                itemServicoGateway, servicoGateway);

        assertThrows(CampoInvalidoException.class, () -> useCase.execute(10L, null, null));
    }

    @Test
    @DisplayName("AdicionarServicosOrdemServicoUseCase deve lançar quando lista de serviços estiver vazia")
    void adicionarServicos_deveLancarQuandoListaVazia() {
        AdicionarServicosOrdemServicoUseCase useCase = new AdicionarServicosOrdemServicoUseCase(ordemServicoGateway,
                itemServicoGateway, servicoGateway);

        assertThrows(CampoInvalidoException.class, () -> useCase.execute(10L, List.of(), null));
    }

    @Test
    @DisplayName("AdicionarServicosOrdemServicoUseCase deve lançar quando ordem não existir")
    void adicionarServicos_deveLancarQuandoOrdemNaoExistir() {
        AdicionarServicosOrdemServicoUseCase useCase = new AdicionarServicosOrdemServicoUseCase(ordemServicoGateway,
                itemServicoGateway, servicoGateway);
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(10L, List.of(1L), null));
    }

    @Test
    @DisplayName("AdicionarServicosOrdemServicoUseCase deve lançar quando serviço já estiver vinculado")
    void adicionarServicos_deveLancarQuandoServicoDuplicado() {
        AdicionarServicosOrdemServicoUseCase useCase = new AdicionarServicosOrdemServicoUseCase(ordemServicoGateway,
                itemServicoGateway, servicoGateway);
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.of(ordemBase()));
        when(itemServicoGateway.existePorOrdemServicoIdEServicoId(10L, 1L)).thenReturn(true);

        assertThrows(RecursoJaExisteException.class, () -> useCase.execute(10L, List.of(1L), null));
    }

    @Test
    @DisplayName("AdicionarServicosOrdemServicoUseCase deve lançar quando serviço não existir")
    void adicionarServicos_deveLancarQuandoServicoNaoExistir() {
        AdicionarServicosOrdemServicoUseCase useCase = new AdicionarServicosOrdemServicoUseCase(ordemServicoGateway,
                itemServicoGateway, servicoGateway);
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.of(ordemBase()));
        when(itemServicoGateway.existePorOrdemServicoIdEServicoId(10L, 1L)).thenReturn(false);
        when(servicoGateway.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(10L, List.of(1L), null));
    }

    @Test
    @DisplayName("AdicionarServicosOrdemServicoUseCase deve aplicar valores parciais e manter preço padrão nos demais")
    void adicionarServicos_deveAplicarValoresParciais() {
        AdicionarServicosOrdemServicoUseCase useCase = new AdicionarServicosOrdemServicoUseCase(ordemServicoGateway,
                itemServicoGateway, servicoGateway);
        OrdemServico ordem = ordemBase();
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.of(ordem));
        when(itemServicoGateway.existePorOrdemServicoIdEServicoId(10L, 1L)).thenReturn(false);
        when(itemServicoGateway.existePorOrdemServicoIdEServicoId(10L, 2L)).thenReturn(false);
        when(servicoGateway.buscarPorId(1L)).thenReturn(
                Optional.of(Servico.builder().id(1L).nome("Lavagem").preco(new BigDecimal("50.00")).build()));
        when(servicoGateway.buscarPorId(2L)).thenReturn(
                Optional.of(Servico.builder().id(2L).nome("Polimento").preco(new BigDecimal("120.00")).build()));

        OrdemServico resultado = useCase.execute(10L, List.of(1L, 2L), List.of(new BigDecimal("99.90")));

        assertEquals(10L, resultado.getId());

        ArgumentCaptor<ItemServico> captor = ArgumentCaptor.forClass(ItemServico.class);
        verify(itemServicoGateway, org.mockito.Mockito.times(2)).salvar(captor.capture());
        List<ItemServico> salvos = captor.getAllValues();
        assertEquals(new BigDecimal("99.90"), salvos.getFirst().getPreco());
        assertEquals(new BigDecimal("120.00"), salvos.get(1).getPreco());
    }

    @Test
    @DisplayName("AdicionarServicosOrdemServicoUseCase deve manter preço original quando valoresAplicados for nulo")
    void adicionarServicos_deveManterPrecoOriginalQuandoValoresAplicadosNulo() {
        AdicionarServicosOrdemServicoUseCase useCase = new AdicionarServicosOrdemServicoUseCase(ordemServicoGateway,
                itemServicoGateway, servicoGateway);
        OrdemServico ordem = ordemBase();
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.of(ordem));
        when(itemServicoGateway.existePorOrdemServicoIdEServicoId(10L, 1L)).thenReturn(false);
        when(servicoGateway.buscarPorId(1L)).thenReturn(
                Optional.of(Servico.builder().id(1L).nome("Lavagem").preco(new BigDecimal("50.00")).build()));

        OrdemServico resultado = useCase.execute(10L, List.of(1L), null);

        assertEquals(10L, resultado.getId());
        ArgumentCaptor<ItemServico> captor = ArgumentCaptor.forClass(ItemServico.class);
        verify(itemServicoGateway).salvar(captor.capture());
        assertEquals(new BigDecimal("50.00"), captor.getValue().getPreco());
    }

    @Test
    @DisplayName("AtualizarValorItemServicoUseCase deve lançar quando valor for nulo")
    void atualizarValor_deveLancarQuandoValorNulo() {
        AtualizarValorItemServicoUseCase useCase = new AtualizarValorItemServicoUseCase(ordemServicoGateway,
                itemServicoGateway);

        assertThrows(CampoInvalidoException.class, () -> useCase.execute(10L, 1L, null));
    }

    @Test
    @DisplayName("AtualizarValorItemServicoUseCase deve lançar quando valor for negativo")
    void atualizarValor_deveLancarQuandoValorNegativo() {
        AtualizarValorItemServicoUseCase useCase = new AtualizarValorItemServicoUseCase(ordemServicoGateway,
                itemServicoGateway);

        assertThrows(CampoInvalidoException.class, () -> useCase.execute(10L, 1L, new BigDecimal("-1.00")));
    }

    @Test
    @DisplayName("AtualizarValorItemServicoUseCase deve lançar quando ordem não existir")
    void atualizarValor_deveLancarQuandoOrdemNaoExistir() {
        AtualizarValorItemServicoUseCase useCase = new AtualizarValorItemServicoUseCase(ordemServicoGateway,
                itemServicoGateway);
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(10L, 1L, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("AtualizarValorItemServicoUseCase deve lançar quando item não existir na ordem")
    void atualizarValor_deveLancarQuandoItemNaoExistir() {
        AtualizarValorItemServicoUseCase useCase = new AtualizarValorItemServicoUseCase(ordemServicoGateway,
                itemServicoGateway);
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.of(ordemBase()));
        when(itemServicoGateway.buscarPorOrdemServicoIdEServicoId(10L, 1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(10L, 1L, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("AtualizarValorItemServicoUseCase deve atualizar preço e salvar")
    void atualizarValor_deveAtualizarPrecoESalvar() {
        AtualizarValorItemServicoUseCase useCase = new AtualizarValorItemServicoUseCase(ordemServicoGateway,
                itemServicoGateway);
        OrdemServico ordem = ordemBase();
        ItemServico item = ItemServico.builder().id(100L).preco(new BigDecimal("50.00")).build();
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.of(ordem));
        when(itemServicoGateway.buscarPorOrdemServicoIdEServicoId(10L, 1L)).thenReturn(Optional.of(item));

        OrdemServico resultado = useCase.execute(10L, 1L, new BigDecimal("75.00"));

        assertEquals(10L, resultado.getId());
        assertEquals(new BigDecimal("75.00"), item.getPreco());
        verify(itemServicoGateway).salvar(item);
    }

    @Test
    @DisplayName("AtualizarStatusOrdemServicoUseCase deve lançar quando status for nulo")
    void atualizarStatus_deveLancarQuandoStatusNulo() {
        AtualizarStatusOrdemServicoUseCase useCase = new AtualizarStatusOrdemServicoUseCase(ordemServicoGateway);

        assertThrows(CampoInvalidoException.class, () -> useCase.execute(10L, null));
    }

    @Test
    @DisplayName("AtualizarStatusOrdemServicoUseCase deve lançar quando ordem não existir")
    void atualizarStatus_deveLancarQuandoOrdemNaoExistir() {
        AtualizarStatusOrdemServicoUseCase useCase = new AtualizarStatusOrdemServicoUseCase(ordemServicoGateway);
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(10L, StatusOrdem.EM_ANDAMENTO.getId()));
    }

    @Test
    @DisplayName("AtualizarStatusOrdemServicoUseCase deve atualizar para concluído e persistir data de conclusão")
    void atualizarStatus_deveAtualizarParaConcluido() {
        AtualizarStatusOrdemServicoUseCase useCase = new AtualizarStatusOrdemServicoUseCase(ordemServicoGateway);
        OrdemServico ordem = ordemBase();
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.of(ordem));
        when(ordemServicoGateway.salvar(ordem)).thenReturn(ordem);

        OrdemServico resultado = useCase.execute(10L, StatusOrdem.CONCLUIDO.getId());

        assertNotNull(resultado.getDtConclusao());
        assertEquals(StatusOrdem.CONCLUIDO.getId(), resultado.getStatus().getId());
        verify(ordemServicoGateway).salvar(ordem);
    }

    @Test
    @DisplayName("BuscarOrdensServicoParaGestaoUseCase deve lançar quando dataInicio for maior que dataFim")
    void buscarOrdensParaGestao_deveLancarQuandoPeriodoInvalido() {
        BuscarOrdensServicoParaGestaoUseCase useCase = new BuscarOrdensServicoParaGestaoUseCase(ordemServicoGateway);

        assertThrows(CampoInvalidoException.class, () -> useCase.execute(1L, LocalDate.of(2026, 4, 5),
                LocalDate.of(2026, 4, 4), org.springframework.data.domain.PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("BuscarOrdensServicoParaGestaoUseCase deve converter datas e delegar ao gateway")
    void buscarOrdensParaGestao_deveConverterDatasEDelegar() {
        BuscarOrdensServicoParaGestaoUseCase useCase = new BuscarOrdensServicoParaGestaoUseCase(ordemServicoGateway);
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        var page = new org.springframework.data.domain.PageImpl<>(List.of(ordemBase()));
        LocalDate dataInicio = LocalDate.of(2026, 4, 1);
        LocalDate dataFim = LocalDate.of(2026, 4, 3);
        when(ordemServicoGateway.buscarTodosParaGestao(2L, dataInicio.atStartOfDay(), dataFim.atTime(23, 59, 59),
                pageable)).thenReturn(page);

        var resultado = useCase.execute(2L, dataInicio, dataFim, pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(ordemServicoGateway).buscarTodosParaGestao(2L, dataInicio.atStartOfDay(), dataFim.atTime(23, 59, 59),
                pageable);
    }

    @Test
    @DisplayName("BuscarOrdensServicoParaGestaoUseCase deve aceitar datas nulas")
    void buscarOrdensParaGestao_deveAceitarDatasNulas() {
        BuscarOrdensServicoParaGestaoUseCase useCase = new BuscarOrdensServicoParaGestaoUseCase(ordemServicoGateway);
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        var page = new org.springframework.data.domain.PageImpl<>(List.<OrdemServico>of());
        when(ordemServicoGateway.buscarTodosParaGestao(null, null, null, pageable)).thenReturn(page);

        var resultado = useCase.execute(null, null, null, pageable);

        assertEquals(0, resultado.getTotalElements());
        verify(ordemServicoGateway).buscarTodosParaGestao(null, null, null, pageable);
    }

    @Test
    @DisplayName("BuscarOrdensServicoParaGestaoUseCase deve aceitar apenas dataFim")
    void buscarOrdensParaGestao_deveAceitarApenasDataFim() {
        BuscarOrdensServicoParaGestaoUseCase useCase = new BuscarOrdensServicoParaGestaoUseCase(ordemServicoGateway);
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        var page = new org.springframework.data.domain.PageImpl<>(List.of(ordemBase()));
        LocalDate dataFim = LocalDate.of(2026, 4, 30);
        when(ordemServicoGateway.buscarTodosParaGestao(3L, null, dataFim.atTime(23, 59, 59), pageable))
                .thenReturn(page);

        var resultado = useCase.execute(3L, null, dataFim, pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(ordemServicoGateway).buscarTodosParaGestao(3L, null, dataFim.atTime(23, 59, 59), pageable);
    }

    @Test
    @DisplayName("BuscarOrdensServicoParaGestaoUseCase deve aceitar apenas dataInicio")
    void buscarOrdensParaGestao_deveAceitarApenasDataInicio() {
        BuscarOrdensServicoParaGestaoUseCase useCase = new BuscarOrdensServicoParaGestaoUseCase(ordemServicoGateway);
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        var page = new org.springframework.data.domain.PageImpl<>(List.of(ordemBase()));
        LocalDate dataInicio = LocalDate.of(2026, 4, 1);
        when(ordemServicoGateway.buscarTodosParaGestao(4L, dataInicio.atStartOfDay(), null, pageable)).thenReturn(page);

        var resultado = useCase.execute(4L, dataInicio, null, pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(ordemServicoGateway).buscarTodosParaGestao(4L, dataInicio.atStartOfDay(), null, pageable);
    }

    @Test
    @DisplayName("AtualizarOrdemServicoUseCase deve atualizar campos e salvar")
    void atualizarOrdemServico_deveAtualizarCamposESalvar() {
        BuscarOrdemServicoComDetalhesUseCase buscarUseCase = org.mockito.Mockito
                .mock(BuscarOrdemServicoComDetalhesUseCase.class);
        AtualizarOrdemServicoUseCase useCase = new AtualizarOrdemServicoUseCase(buscarUseCase, ordemServicoEventGateway,
                ordemServicoGateway);
        OrdemServico ordem = ordemBase();
        LocalDateTime novaData = LocalDateTime.of(2026, 5, 10, 14, 0);
        when(buscarUseCase.execute(10L)).thenReturn(ordem);
        when(ordemServicoGateway.salvar(ordem)).thenReturn(ordem);

        OrdemServico resultado = useCase.execute(10L, novaData, new BigDecimal("199.90"), "observacao atualizada",
                StatusOrdem.EM_ANDAMENTO.getId(), null);

        assertEquals(novaData, resultado.getDataAgendamento());
        assertEquals(new BigDecimal("199.90"), resultado.getPrecoMinimo());
        assertEquals("observacao atualizada", resultado.getObservacoes());
        assertEquals(StatusOrdem.EM_ANDAMENTO.getId(), resultado.getStatus().getId());
        verify(ordemServicoGateway).salvar(ordem);
    }

    @Test
    @DisplayName("AtualizarOrdemServicoUseCase deve preencher data de conclusao ao concluir ordem")
    void atualizarOrdemServico_devePreencherDataConclusaoAoConcluir() {
        BuscarOrdemServicoComDetalhesUseCase buscarUseCase = org.mockito.Mockito
                .mock(BuscarOrdemServicoComDetalhesUseCase.class);
        AtualizarOrdemServicoUseCase useCase = new AtualizarOrdemServicoUseCase(buscarUseCase, ordemServicoEventGateway,
                ordemServicoGateway);
        OrdemServico ordem = ordemBase();
        when(buscarUseCase.execute(10L)).thenReturn(ordem);
        when(ordemServicoGateway.salvar(ordem)).thenReturn(ordem);

        OrdemServico resultado = useCase.execute(10L, null, null, null, StatusOrdem.CONCLUIDO.getId(), null);

        assertNotNull(resultado.getDtConclusao());
        assertEquals(StatusOrdem.CONCLUIDO.getId(), resultado.getStatus().getId());
        verify(ordemServicoGateway).salvar(ordem);
    }
}
