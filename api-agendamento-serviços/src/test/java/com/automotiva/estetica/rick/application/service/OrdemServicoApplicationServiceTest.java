package com.automotiva.estetica.rick.application.service;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.assembler.OrdemServicoResponseAssembler;
import com.automotiva.estetica.rick.application.dto.request.*;
import com.automotiva.estetica.rick.application.dto.response.HorarioDisponivelResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoDetalheResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResponse;
import com.automotiva.estetica.rick.domain.entity.HorarioDisponivel;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.entity.Status;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.DataInvalidaException;
import com.automotiva.estetica.rick.domain.exception.IntegracaoException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import com.automotiva.estetica.rick.domain.usecase.AdicionarServicosOrdemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.AtualizarOrdemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.AtualizarStatusOrdemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.AtualizarValorItemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarOrdemServicoComDetalhesUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarHorariosDisponiveisUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarOrdemServicoPorIdUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarOrdensServicoParaGestaoUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarOrdensServicoPorUsuarioUseCase;
import com.automotiva.estetica.rick.domain.usecase.CriarOrdemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.LimparCarrinhoPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarOrdensServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.NotificarAtualizacaoOrdemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.RemoverServicoOrdemServicoUseCase;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
class OrdemServicoApplicationServiceTest {

    @Mock
    private ItemServicoGateway itemServicoGateway;

    @Mock
    private LimparCarrinhoPessoaUseCase limparCarrinhoPessoaUseCase;

    @Mock
    private CriarOrdemServicoUseCase criarOrdemServicoUseCase;

    @Mock
    private AtualizarOrdemServicoUseCase atualizarOrdemServicoUseCase;

    @Mock
    private AtualizarStatusOrdemServicoUseCase atualizarStatusOrdemServicoUseCase;

    @Mock
    private AdicionarServicosOrdemServicoUseCase adicionarServicosOrdemServicoUseCase;

    @Mock
    private AtualizarValorItemServicoUseCase atualizarValorItemServicoUseCase;

    @Mock
    private RemoverServicoOrdemServicoUseCase removerServicoOrdemServicoUseCase;

    @Mock
    private BuscarOrdensServicoParaGestaoUseCase buscarOrdensServicoParaGestaoUseCase;

    @Mock
    private BuscarOrdemServicoComDetalhesUseCase buscarOrdemServicoComDetalhesUseCase;

    @Mock
    private ListarOrdensServicoUseCase listarOrdensServicoUseCase;

    @Mock
    private BuscarOrdemServicoPorIdUseCase buscarOrdemServicoPorIdUseCase;

    @Mock
    private BuscarOrdensServicoPorUsuarioUseCase buscarOrdensServicoPorUsuarioUseCase;

    @Mock
    private BuscarHorariosDisponiveisUseCase buscarHorariosDisponiveisUseCase;

    @Mock
    private NotificarAtualizacaoOrdemServicoUseCase notificarAtualizacaoOrdemServicoUseCase;

    @Spy
    private OrdemServicoResponseAssembler ordemServicoResponseAssembler = new OrdemServicoResponseAssembler();

    @InjectMocks
    private OrdemServicoApplicationService ordemServicoApplicationService;

    private MockedStatic<LocalDate> mockedDate;
    @BeforeEach
    void setup() {
        LocalDate now = LocalDate.now();
        mockedDate = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS);
        mockedDate.when(LocalDate::now).thenReturn(now);
    }

    @AfterEach
    void tearDown() {
        mockedDate.close();
    }

    private OrdemServico ordemMock() {
        Veiculo veiculo = Veiculo.builder().id(1L).build();
        Status status = Status.builder().id(1L).build();
        return OrdemServico.builder().id(10L).dataAgendamento(LocalDateTime.now()).precoMinimo(BigDecimal.valueOf(100))
                .veiculo(veiculo).status(status).build();
    }

    private List<ItemServico> itensMock(OrdemServico ordem) {
        Servico lavagem = Servico.builder().id(1L).nome("Lavagem").preco(BigDecimal.valueOf(50)).build();
        Servico polimento = Servico.builder().id(2L).nome("Polimento").preco(BigDecimal.valueOf(120)).build();

        ItemServico item1 = ItemServico.builder().id(100L).ordemServico(ordem).servico(lavagem)
                .preco(BigDecimal.valueOf(55)).build();

        ItemServico item2 = ItemServico.builder().id(101L).ordemServico(ordem).servico(polimento)
                .preco(BigDecimal.valueOf(125)).build();

        return List.of(item1, item2);
    }

    @Test
    @DisplayName("Deve retornar lista de ordens de serviço por usuário")
    void buscarPorUsuarioId_sucesso() {
        OrdemServico ordem = ordemMock();

        when(buscarOrdensServicoPorUsuarioUseCase.execute(1L)).thenReturn(List.of(ordem));
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        List<OrdemServicoResponse> resultado = ordemServicoApplicationService.buscarPorUsuarioId(1L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.getFirst().getId());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não possui ordens de serviço")
    void buscarPorUsuarioId_listaVazia_deveRetornarListaVazia() {
        when(buscarOrdensServicoPorUsuarioUseCase.execute(1L)).thenReturn(emptyList());

        List<OrdemServicoResponse> resultado = ordemServicoApplicationService.buscarPorUsuarioId(1L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar horarios disponiveis para agendamento")
    void buscarHorariosDisponiveis_sucesso() {
        LocalDate data = LocalDate.of(2025, 12, 1);
        List<Long> servicosIds = List.of(1L, 2L);

        when(buscarHorariosDisponiveisUseCase.execute(data, servicosIds))
                .thenReturn(List.of(new HorarioDisponivel(LocalTime.of(9, 0), LocalTime.of(11, 0))));

        List<HorarioDisponivelResponse> resultado = ordemServicoApplicationService.buscarHorariosDisponiveis(data,
                servicosIds);

        assertEquals(1, resultado.size());
        assertEquals(LocalTime.of(9, 0), resultado.getFirst().inicio());
        assertEquals(LocalTime.of(11, 0), resultado.getFirst().fim());
        verify(buscarHorariosDisponiveisUseCase).execute(data, servicosIds);
    }

    @Test
    @DisplayName("Deve propagar excecao quando servicos nao forem encontrados")
    void buscarHorariosDisponiveis_servicosNaoEncontrados_devePropagarExcecao() {
        LocalDate data = LocalDate.now();
        List<Long> servicosIds = List.of(9999L);
        RecursoNaoEncontradoException ex = RecursoNaoEncontradoException.builder().mensagem("Servico nao encontrado")
                .detalhes("").build();
        when(buscarHorariosDisponiveisUseCase.execute(data, servicosIds)).thenThrow(ex);

        RecursoNaoEncontradoException thrown = assertThrows(RecursoNaoEncontradoException.class,
                () -> ordemServicoApplicationService.buscarHorariosDisponiveis(data, servicosIds));

        assertSame(ex, thrown);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a data solicitada for anterior à atual")
    void buscarHorariosDisponiveis_dataAnteriorAtual_devePropagarExcecao() {
        LocalDate data = LocalDate.of(2025, 12, 1);
        List<Long> servicosIds = List.of(9999L);
        DataInvalidaException ex = DataInvalidaException.builder().mensagem("Data inválida").detalhes("").build();

        when(buscarHorariosDisponiveisUseCase.execute(data, servicosIds)).thenThrow(ex);

        DataInvalidaException thrown = assertThrows(DataInvalidaException.class,
                () -> ordemServicoApplicationService.buscarHorariosDisponiveis(data, servicosIds));

        assertSame(ex, thrown);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ordem por ID inexistente")
    void buscarPorId_inexistente_deveLancarExcecao() {
        when(buscarOrdemServicoPorIdUseCase.execute(99L))
                .thenThrow(RecursoNaoEncontradoException.builder().mensagem("nao encontrado").detalhes("").build());

        assertThrows(RecursoNaoEncontradoException.class, () -> ordemServicoApplicationService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve retornar ordem de serviço por ID com sucesso")
    void buscarPorId_sucesso() {
        OrdemServico ordem = ordemMock();

        when(buscarOrdemServicoPorIdUseCase.execute(10L)).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoResponse response = ordemServicoApplicationService.buscarPorId(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
    }

    @Test
    @DisplayName("Deve retornar página de ordens ao buscar todos")
    void buscarTodos_sucesso() {
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemMock()));

        when(listarOrdensServicoUseCase.execute(isNull(), any(Pageable.class))).thenReturn(page);

        PageRequest req = new PageRequest();
        req.setPagina(0);
        req.setTamanho(10);

        Page<OrdemServicoResponse> resultado = ordemServicoApplicationService.buscarTodos(req);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deve lançar CampoInvalidoException ao criar ordem sem serviços")
    void criar_semServicos_deveLancarExcecao() {
        OrdemServicoRequest request = OrdemServicoRequest.builder().veiculo(1L)
                .dataAgendamento(LocalDateTime.now().plusDays(1)).build();

        when(criarOrdemServicoUseCase.execute(any(), any(), any(), any(), any()))
                .thenThrow(CampoInvalidoException.builder().mensagem("invalido").detalhes("").build());

        assertThrows(CampoInvalidoException.class, () -> ordemServicoApplicationService.criar(request));
    }

    @Test
    @DisplayName("Deve limpar carrinho quando ordem criada possuir pessoa no veiculo")
    void criar_comPessoaNoVeiculo_deveLimparCarrinho() {
        OrdemServicoRequest request = OrdemServicoRequest.builder().veiculo(1L)
                .dataAgendamento(LocalDateTime.now().plusDays(1)).servicos(List.of(1L)).build();
        OrdemServico ordem = OrdemServico.builder().id(30L)
                .veiculo(Veiculo.builder().id(1L)
                        .pessoa(com.automotiva.estetica.rick.domain.entity.Pessoa.builder().id(99L).build()).build())
                .status(Status.builder().id(1L).build()).build();

        when(criarOrdemServicoUseCase.execute(any(), any(), any(), any(), any())).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(30L)).thenReturn(emptyList());

        OrdemServicoResponse response = ordemServicoApplicationService.criar(request);

        assertEquals(30L, response.getId());
        verify(limparCarrinhoPessoaUseCase).execute(99L);
    }

    @Test
    @DisplayName("Deve atualizar status da ordem no fluxo de gestão")
    void atualizarStatusParaGestao_sucesso() {
        OrdemServico ordem = ordemMock();
        AtualizarOrdemServicoGestaoRequest request = AtualizarOrdemServicoGestaoRequest.builder().status(1L).build();

        when(atualizarOrdemServicoUseCase.execute(anyLong(), any(), any(), any(), anyLong(), any())).thenReturn(ordem);
        when(buscarOrdemServicoComDetalhesUseCase.execute(10L)).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoDetalheResponse response = ordemServicoApplicationService.atualizarParaGestao(10L, request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertNotNull(response.getStatus());
        assertEquals(1L, response.getStatus().getId());
        verify(notificarAtualizacaoOrdemServicoUseCase).execute(ordem);
    }

    @Test
    @DisplayName("Deve enviar status nulo quando request for nulo na gestão")
    void atualizarStatusParaGestao_requestNulo_deveEnviarStatusNulo() {
        OrdemServico ordem = ordemMock();
        when(atualizarOrdemServicoUseCase.execute(anyLong(), any(), any(), any(), any(), any())).thenReturn(ordem);
        when(buscarOrdemServicoComDetalhesUseCase.execute(10L)).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoDetalheResponse response = ordemServicoApplicationService.atualizarParaGestao(10L, null);

        assertEquals(10L, response.getId());
        verify(atualizarOrdemServicoUseCase).execute(eq(10L), isNull(), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    @DisplayName("Deve lançar CampoInvalidoException ao atualizar valor com número negativo")
    void atualizarValorServicoParaGestao_valorNegativo_deveLancarExcecao() {
        AtualizarValorServicoOrdemRequest request = AtualizarValorServicoOrdemRequest.builder()
                .valorAplicado(BigDecimal.valueOf(-1)).build();

        doThrow(CampoInvalidoException.builder().mensagem("valor invalido").detalhes("").build())
                .when(atualizarValorItemServicoUseCase).execute(10L, 1L, BigDecimal.valueOf(-1));

        assertThrows(CampoInvalidoException.class,
                () -> ordemServicoApplicationService.atualizarValorServicoParaGestao(10L, 1L, request));
    }

    @Test
    @DisplayName("Deve enviar valor nulo quando request de atualização vier nulo")
    void atualizarValorServicoParaGestao_requestNulo_deveEnviarValorNulo() {
        OrdemServico ordem = ordemMock();
        when(buscarOrdemServicoComDetalhesUseCase.execute(10L)).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoDetalheResponse response = ordemServicoApplicationService.atualizarValorServicoParaGestao(10L, 1L,
                null);

        assertEquals(10L, response.getId());
        verify(atualizarValorItemServicoUseCase).execute(10L, 1L, null);
    }

    @Test
    @DisplayName("Deve retornar ordem com serviços detalhados")
    void buscarPorId_deveRetornarServicosDetalhados() {
        OrdemServico ordem = ordemMock();

        when(buscarOrdemServicoPorIdUseCase.execute(10L)).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(itensMock(ordem));

        OrdemServicoResponse response = ordemServicoApplicationService.buscarPorId(10L);

        assertNotNull(response);
        assertNotNull(response.getServicos());
        assertEquals(2, response.getServicos().size());

        var primeiroServico = response.getServicos().getFirst();
        assertEquals(1L, primeiroServico.getId());
        assertEquals("Lavagem", primeiroServico.getNome());
        assertEquals(BigDecimal.valueOf(55), primeiroServico.getValorAplicado());
        assertEquals(BigDecimal.valueOf(50), primeiroServico.getPreco());
    }

    @Test
    @DisplayName("Deve buscar ordens para gestao com filtros e paginacao")
    void buscarTodosParaGestao_sucesso() {
        OrdemServico ordem = ordemMock();
        Page<OrdemServico> page = new PageImpl<>(List.of(ordem));
        when(buscarOrdensServicoParaGestaoUseCase.execute(any(), any(), any(), any(Pageable.class))).thenReturn(page);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoGestaoPageRequest request = OrdemServicoGestaoPageRequest.builder().status(1L)
                .dataInicio(LocalDate.now().minusDays(7)).dataFim(LocalDate.now()).pagina(0).tamanho(10)
                .ordenarPor("dataAgendamento").direcao("desc").build();

        Page<OrdemServicoResumoResponse> resultado = ordemServicoApplicationService.buscarTodosParaGestao(request);

        assertEquals(1, resultado.getTotalElements());
        verify(buscarOrdensServicoParaGestaoUseCase).execute(eq(1L), any(LocalDate.class), any(LocalDate.class),
                any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar detalhe para gestao")
    void buscarDetalheParaGestao_sucesso() {
        OrdemServico ordem = ordemMock();
        when(buscarOrdemServicoComDetalhesUseCase.execute(10L)).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoDetalheResponse response = ordemServicoApplicationService.buscarDetalheParaGestao(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
    }

    @Test
    @DisplayName("Deve adicionar servicos para gestao e mapear ids e valores")
    void adicionarServicosParaGestao_sucesso() {
        OrdemServico ordem = ordemMock();
        AdicionarServicosOrdemRequest request = AdicionarServicosOrdemRequest.builder()
                .servicos(List.of(ServicoAplicadoRequest.builder().idServico(1L).valorAplicado(BigDecimal.TEN).build(),
                        ServicoAplicadoRequest.builder().idServico(2L).valorAplicado(BigDecimal.ONE).build()))
                .build();

        when(buscarOrdemServicoComDetalhesUseCase.execute(10L)).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoDetalheResponse response = ordemServicoApplicationService.adicionarServicosParaGestao(10L, request);

        verify(adicionarServicosOrdemServicoUseCase).execute(10L, List.of(1L, 2L),
                List.of(BigDecimal.TEN, BigDecimal.ONE));
        assertEquals(10L, response.getId());
    }

    @Test
    @DisplayName("Deve tratar request nulo ao adicionar servicos para gestao")
    void adicionarServicosParaGestao_requestNulo_deveEnviarListasVazias() {
        OrdemServico ordem = ordemMock();
        when(buscarOrdemServicoComDetalhesUseCase.execute(10L)).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        ordemServicoApplicationService.adicionarServicosParaGestao(10L, null);

        verify(adicionarServicosOrdemServicoUseCase).execute(10L, List.of(), List.of());
    }

    @Test
    @DisplayName("Deve remover servico para gestao")
    void removerServicoParaGestao_sucesso() {
        OrdemServico ordem = ordemMock();
        when(buscarOrdemServicoComDetalhesUseCase.execute(10L)).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoDetalheResponse response = ordemServicoApplicationService.removerServicoParaGestao(10L, 1L);

        verify(removerServicoOrdemServicoUseCase).execute(10L, 1L);
        assertEquals(10L, response.getId());
    }

    @Test
    @DisplayName("Deve atualizar ordem e notificar quando alteracao ocorrer")
    void atualizar_sucesso() {
        OrdemServico ordem = ordemMock();
        OrdemServicoRequest request = OrdemServicoRequest.builder().dataAgendamento(LocalDateTime.now().plusDays(1))
                .precoMinimo(BigDecimal.valueOf(200)).observacoes("ok").status(1L).motivo(null).build();

        when(atualizarOrdemServicoUseCase.execute(eq(10L), any(LocalDateTime.class), any(BigDecimal.class), eq("ok"),
                eq(1L), isNull())).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoResponse response = ordemServicoApplicationService.atualizar(10L, request);

        assertEquals(10L, response.getId());
        verify(notificarAtualizacaoOrdemServicoUseCase).execute(ordem);
    }

    @Test
    @DisplayName("Deve converter excecao generica em IntegracaoException ao criar")
    void criar_quandoFalhaGenerica_deveLancarIntegracaoException() {
        OrdemServicoRequest request = OrdemServicoRequest.builder().veiculo(1L).dataAgendamento(LocalDateTime.now())
                .servicos(List.of(1L)).build();

        when(criarOrdemServicoUseCase.execute(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("boom"));

        assertThrows(IntegracaoException.class, () -> ordemServicoApplicationService.criar(request));
    }

    @Test
    @DisplayName("Deve propagar DomainException sem encapsular em IntegracaoException ao criar")
    void criar_quandoDomainException_devePropagarExcecaoOriginal() {
        OrdemServicoRequest request = OrdemServicoRequest.builder().veiculo(1L).dataAgendamento(LocalDateTime.now())
                .servicos(List.of(1L)).build();
        RecursoNaoEncontradoException domainEx = RecursoNaoEncontradoException.builder().mensagem("nao encontrado")
                .detalhes("veiculo").build();

        when(criarOrdemServicoUseCase.execute(any(), any(), any(), any(), any())).thenThrow(domainEx);

        RecursoNaoEncontradoException thrown = assertThrows(RecursoNaoEncontradoException.class,
                () -> ordemServicoApplicationService.criar(request));

        assertSame(domainEx, thrown);
        verify(limparCarrinhoPessoaUseCase, never()).execute(anyLong());
    }

    @Test
    @DisplayName("Nao deve limpar carrinho quando pessoa da ordem nao estiver disponivel")
    void criar_semPessoaNoVeiculo_naoDeveLimparCarrinho() {
        OrdemServicoRequest request = OrdemServicoRequest.builder().veiculo(1L).dataAgendamento(LocalDateTime.now())
                .servicos(List.of(1L)).build();
        OrdemServico ordemSemPessoa = OrdemServico.builder().id(20L).veiculo(Veiculo.builder().id(1L).build())
                .status(Status.builder().id(1L).build()).build();

        when(criarOrdemServicoUseCase.execute(any(), any(), any(), any(), any())).thenReturn(ordemSemPessoa);
        when(itemServicoGateway.buscarPorOrdemServicoId(20L)).thenReturn(emptyList());

        OrdemServicoResponse response = ordemServicoApplicationService.criar(request);

        assertEquals(20L, response.getId());
        verify(limparCarrinhoPessoaUseCase, never()).execute(anyLong());
    }

    @Test
    @DisplayName("Nao deve limpar carrinho quando veiculo da ordem for nulo")
    void criar_semVeiculo_naoDeveLimparCarrinho() {
        OrdemServicoRequest request = OrdemServicoRequest.builder().veiculo(1L).dataAgendamento(LocalDateTime.now())
                .servicos(List.of(1L)).build();
        OrdemServico ordemSemVeiculo = OrdemServico.builder().id(21L).veiculo(null)
                .status(Status.builder().id(1L).build()).build();

        when(criarOrdemServicoUseCase.execute(any(), any(), any(), any(), any())).thenReturn(ordemSemVeiculo);
        when(itemServicoGateway.buscarPorOrdemServicoId(21L)).thenReturn(emptyList());

        OrdemServicoResponse response = ordemServicoApplicationService.criar(request);

        assertEquals(21L, response.getId());
        verify(limparCarrinhoPessoaUseCase, never()).execute(anyLong());
    }

    @Test
    @DisplayName("Nao deve limpar carrinho quando pessoa da ordem tiver id nulo")
    void criar_pessoaSemId_naoDeveLimparCarrinho() {
        OrdemServicoRequest request = OrdemServicoRequest.builder().veiculo(1L).dataAgendamento(LocalDateTime.now())
                .servicos(List.of(1L)).build();
        OrdemServico ordemPessoaSemId = OrdemServico.builder().id(22L)
                .veiculo(Veiculo.builder().id(1L)
                        .pessoa(com.automotiva.estetica.rick.domain.entity.Pessoa.builder().build()).build())
                .status(Status.builder().id(1L).build()).build();

        when(criarOrdemServicoUseCase.execute(any(), any(), any(), any(), any())).thenReturn(ordemPessoaSemId);
        when(itemServicoGateway.buscarPorOrdemServicoId(22L)).thenReturn(emptyList());

        OrdemServicoResponse response = ordemServicoApplicationService.criar(request);

        assertEquals(22L, response.getId());
        verify(limparCarrinhoPessoaUseCase, never()).execute(anyLong());
    }

    @Test
    @DisplayName("Deve tratar request sem lista de servicos ao adicionar para gestao")
    void adicionarServicosParaGestao_listaNula_deveEnviarListasVazias() {
        OrdemServico ordem = ordemMock();
        AdicionarServicosOrdemRequest request = AdicionarServicosOrdemRequest.builder().servicos(null).build();
        when(buscarOrdemServicoComDetalhesUseCase.execute(10L)).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        ordemServicoApplicationService.adicionarServicosParaGestao(10L, request);

        verify(adicionarServicosOrdemServicoUseCase).execute(10L, List.of(), List.of());
    }

    @Test
    @DisplayName("Deve atualizar valor aplicado na gestão quando request for válido")
    void atualizarValorServicoParaGestao_sucesso() {
        OrdemServico ordem = ordemMock();
        AtualizarValorServicoOrdemRequest request = AtualizarValorServicoOrdemRequest.builder()
                .valorAplicado(BigDecimal.valueOf(77)).build();
        when(buscarOrdemServicoComDetalhesUseCase.execute(10L)).thenReturn(ordem);
        when(itemServicoGateway.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoDetalheResponse response = ordemServicoApplicationService.atualizarValorServicoParaGestao(10L, 1L,
                request);

        assertEquals(10L, response.getId());
        verify(atualizarValorItemServicoUseCase).execute(10L, 1L, BigDecimal.valueOf(77));
    }
}
