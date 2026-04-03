package com.automotiva.estetica.rick.application.service;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.dto.request.AtualizarStatusOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.AtualizarValorServicoOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoDetalheResponse;
import com.automotiva.estetica.rick.application.dto.response.HorarioDisponivelResponse;
import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.OrdemServicoDuracaoProjection;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResponse;
import com.automotiva.estetica.rick.application.port.in.CarrinhoUseCase;
import com.automotiva.estetica.rick.application.port.out.EmailPort;
import com.automotiva.estetica.rick.application.port.out.ItemServicoRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.OrdemServicoEventPublisherPort;
import com.automotiva.estetica.rick.application.port.out.OrdemServicoRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.ServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.entity.Status;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
class OrdemServicoServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @Mock
    private ItemServicoRepositoryPort itemServicoRepositoryPort;

    @Mock
    private ServicoRepositoryPort servicoRepositoryPort;

    @Mock
    private CarrinhoUseCase carrinhoUseCase;

    @Mock
    private OrdemServicoEventPublisherPort ordemServicoPublisher;

    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private OrdemServicoService ordemServicoService;

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

        when(ordemServicoRepositoryPort.buscarPorVeiculoPessoaId(1L)).thenReturn(List.of(ordem));
        when(itemServicoRepositoryPort.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        List<OrdemServicoResponse> resultado = ordemServicoService.buscarPorUsuarioId(1L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.getFirst().getId());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não possui ordens de serviço")
    void buscarPorUsuarioId_listaVazia_deveRetornarListaVazia() {
        when(ordemServicoRepositoryPort.buscarPorVeiculoPessoaId(1L)).thenReturn(emptyList());

        List<OrdemServicoResponse> resultado = ordemServicoService.buscarPorUsuarioId(1L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ordem por ID inexistente")
    void buscarPorId_inexistente_deveLancarExcecao() {
        when(ordemServicoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class, () -> ordemServicoService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve retornar ordem de serviço por ID com sucesso")
    void buscarPorId_sucesso() {
        OrdemServico ordem = ordemMock();

        when(ordemServicoRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(ordem));
        when(itemServicoRepositoryPort.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoResponse response = ordemServicoService.buscarPorId(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
    }

    @Test
    @DisplayName("Deve retornar página de ordens ao buscar todos")
    void buscarTodos_sucesso() {
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemMock()));

        when(ordemServicoRepositoryPort.buscarTodos(isNull(), any(Pageable.class))).thenReturn(page);

        PageRequest req = new PageRequest();
        req.setPagina(0);
        req.setTamanho(10);

        Page<OrdemServicoResponse> resultado = ordemServicoService.buscarTodos(req);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deve retornar horários disponíveis quando não há ordens no dia")
    void buscarHorariosDisponiveis_quandoNaoHaOrdens_deveRetornarHorariosDisponiveis() {
        LocalDate data = LocalDate.of(2026, 3, 17);
        List<Long> servicosIds = List.of(1L);

        Servico servico = new Servico();
        servico.setDuracaoMinutos(60);

        when(servicoRepositoryPort.buscarPorIds(servicosIds)).thenReturn(List.of(servico));

        when(ordemServicoRepositoryPort.buscarDuracaoTotalPorOS(data)).thenReturn(Collections.emptyList());

        List<HorarioDisponivelResponse> resultado = ordemServicoService.buscarHorariosDisponiveis(data, servicosIds);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar horários disponíveis quando há ordens no dia")
    void buscarHorariosDisponiveis_quandoHaOrdensNoDia_deveRetornarHorariosDisponiveis() {
        LocalDate data = LocalDate.of(2026, 3, 17);
        List<Long> servicosIds = List.of(1L);

        Servico servico = new Servico();
        servico.setDuracaoMinutos(60);

        when(servicoRepositoryPort.buscarPorIds(servicosIds)).thenReturn(List.of(servico));

        OrdemServicoDuracaoProjection os1 = criarOS(10, 0, 60);
        OrdemServicoDuracaoProjection os2 = criarOS(13, 0, 60);

        when(ordemServicoRepositoryPort.buscarDuracaoTotalPorOS(data)).thenReturn(List.of(os1, os2));

        List<HorarioDisponivelResponse> resultado = ordemServicoService.buscarHorariosDisponiveis(data, servicosIds);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(4, resultado.size());

        assertEquals(LocalTime.of(9, 0), resultado.get(0).inicio());
        assertEquals(LocalTime.of(10, 0), resultado.get(0).fim());

        assertEquals(LocalTime.of(11, 10), resultado.get(1).inicio());
        assertEquals(LocalTime.of(12, 10), resultado.get(1).fim());

        assertEquals(LocalTime.of(14, 10), resultado.get(2).inicio());
        assertEquals(LocalTime.of(15, 10), resultado.get(2).fim());

        assertEquals(LocalTime.of(15, 20), resultado.get(3).inicio());
        assertEquals(LocalTime.of(16, 20), resultado.get(3).fim());
    }

    private OrdemServicoDuracaoProjection criarOS(int hora, int minuto, long duracao) {
        OrdemServicoDuracaoProjection os = mock(OrdemServicoDuracaoProjection.class);
        when(os.getDataAgendamento()).thenReturn(LocalDateTime.of(2026, 3, 17, hora, minuto));
        when(os.getDuracaoTotal()).thenReturn(duracao);
        return os;
    }

    @Test
    @DisplayName("Deve lançar CampoInvalidoException ao criar ordem sem serviços")
    void criar_semServicos_deveLancarExcecao() {
        OrdemServicoRequest request = OrdemServicoRequest.builder().veiculo(1L)
                .dataAgendamento(LocalDateTime.now().plusDays(1)).build();

        assertThrows(CampoInvalidoException.class, () -> ordemServicoService.criar(request));
    }

    @Test
    @DisplayName("Deve atualizar status da ordem no fluxo de gestão")
    void atualizarStatusParaGestao_sucesso() {
        OrdemServico ordem = ordemMock();
        AtualizarStatusOrdemRequest request = AtualizarStatusOrdemRequest.builder().status(1L).build();

        when(ordemServicoRepositoryPort.buscarPorIdComDetalhes(10L)).thenReturn(Optional.of(ordem));
        when(ordemServicoRepositoryPort.salvar(any(OrdemServico.class))).thenReturn(ordem);
        when(itemServicoRepositoryPort.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoDetalheResponse response = ordemServicoService.atualizarStatusParaGestao(10L, request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertNotNull(response.getStatus());
        assertEquals(1L, response.getStatus().getId());
        verify(ordemServicoRepositoryPort).salvar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar CampoInvalidoException ao atualizar valor com número negativo")
    void atualizarValorServicoParaGestao_valorNegativo_deveLancarExcecao() {
        AtualizarValorServicoOrdemRequest request = AtualizarValorServicoOrdemRequest.builder()
                .valorAplicado(BigDecimal.valueOf(-1)).build();

        assertThrows(CampoInvalidoException.class,
                () -> ordemServicoService.atualizarValorServicoParaGestao(10L, 1L, request));
    }

    @Test
    @DisplayName("Deve retornar ordem com serviços detalhados")
    void buscarPorId_deveRetornarServicosDetalhados() {
        OrdemServico ordem = ordemMock();

        when(ordemServicoRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(ordem));
        when(itemServicoRepositoryPort.buscarPorOrdemServicoId(10L)).thenReturn(itensMock(ordem));

        OrdemServicoResponse response = ordemServicoService.buscarPorId(10L);

        assertNotNull(response);
        assertNotNull(response.getServicos());
        assertEquals(2, response.getServicos().size());

        var primeiroServico = response.getServicos().getFirst();
        assertEquals(1L, primeiroServico.getId());
        assertEquals("Lavagem", primeiroServico.getNome());
        assertEquals(BigDecimal.valueOf(55), primeiroServico.getValorAplicado());
        assertEquals(BigDecimal.valueOf(50), primeiroServico.getPreco());
    }
}
