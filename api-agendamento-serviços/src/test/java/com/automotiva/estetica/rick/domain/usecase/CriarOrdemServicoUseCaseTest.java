package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.entity.Status;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoEventGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CriarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ItemServicoGateway itemServicoGateway;

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private OrdemServicoEventGateway ordemServicoEventGateway;

    @InjectMocks
    private CriarOrdemServicoUseCase useCase;

    @Test
    @DisplayName("deve publicar evento com servicos quando criacao for bem sucedida")
    void execute_devePublicarEvento() {
        LocalDateTime data = LocalDateTime.of(2026, 12, 1, 10, 0);
        List<Long> servicoIds = List.of(1L, 2L);

        OrdemServico ordemSalva = OrdemServico.builder().id(10L).dataAgendamento(data)
                .precoMinimo(BigDecimal.valueOf(100)).veiculo(Veiculo.builder().id(5L).build())
                .status(Status.builder().id(1L).build()).build();

        OrdemServico ordemComDetalhes = OrdemServico.builder().id(10L).dataAgendamento(data)
                .precoMinimo(BigDecimal.valueOf(100)).veiculo(Veiculo.builder().id(5L).placa("ABC1D23").build())
                .status(Status.builder().id(1L).build()).build();

        when(ordemServicoGateway.existePorVeiculoIdEDataAgendamento(5L, data)).thenReturn(false);
        when(ordemServicoGateway.salvar(any(OrdemServico.class))).thenReturn(ordemSalva);
        when(servicoGateway.buscarPorId(1L)).thenReturn(Optional.of(Servico.builder().id(1L).nome("Lavagem").build()));
        when(servicoGateway.buscarPorId(2L))
                .thenReturn(Optional.of(Servico.builder().id(2L).nome("Polimento").build()));
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.of(ordemComDetalhes));

        OrdemServico resultado = useCase.execute(data, BigDecimal.valueOf(100), 5L, "Obs", servicoIds);

        assertEquals(10L, resultado.getId());
        verify(itemServicoGateway, times(2)).salvar(any());

        ArgumentCaptor<List<String>> nomesCaptor = ArgumentCaptor.forClass(List.class);
        verify(ordemServicoEventGateway).publicarOrdemServicoCriada(any(OrdemServico.class), nomesCaptor.capture());
        assertEquals(List.of("Lavagem", "Polimento"), nomesCaptor.getValue());
    }

    @Test
    @DisplayName("deve rejeitar criacao sem servicos")
    void execute_semServicos_deveLancarCampoInvalido() {
        LocalDateTime data = LocalDateTime.of(2026, 12, 1, 10, 0);

        assertThrows(CampoInvalidoException.class, () -> useCase.execute(data, BigDecimal.TEN, 5L, null, List.of()));

        verify(ordemServicoGateway, never()).salvar(any());
        verify(ordemServicoEventGateway, never()).publicarOrdemServicoCriada(any(), any());
    }

    @Test
    @DisplayName("deve rejeitar criacao com lista de servicos nula")
    void execute_servicosNulos_deveLancarCampoInvalido() {
        LocalDateTime data = LocalDateTime.of(2026, 12, 1, 10, 0);

        assertThrows(CampoInvalidoException.class, () -> useCase.execute(data, BigDecimal.TEN, 5L, null, null));

        verify(ordemServicoGateway, never()).salvar(any());
        verify(ordemServicoEventGateway, never()).publicarOrdemServicoCriada(any(), any());
    }

    @Test
    @DisplayName("deve rejeitar conflito de agendamento")
    void execute_comConflito_deveLancarRecursoJaExiste() {
        LocalDateTime data = LocalDateTime.of(2026, 12, 1, 10, 0);
        when(ordemServicoGateway.existePorVeiculoIdEDataAgendamento(5L, data)).thenReturn(true);

        assertThrows(RecursoJaExisteException.class,
                () -> useCase.execute(data, BigDecimal.TEN, 5L, null, List.of(1L)));

        verify(ordemServicoGateway, never()).salvar(any());
        verify(ordemServicoEventGateway, never()).publicarOrdemServicoCriada(any(), any());
    }

    @Test
    @DisplayName("deve lancar quando algum servico nao for encontrado")
    void execute_servicoNaoEncontrado_deveLancarRecursoNaoEncontrado() {
        LocalDateTime data = LocalDateTime.of(2026, 12, 1, 10, 0);
        OrdemServico ordemSalva = OrdemServico.builder().id(10L).dataAgendamento(data)
                .veiculo(Veiculo.builder().id(5L).build()).status(Status.builder().id(1L).build()).build();

        when(ordemServicoGateway.existePorVeiculoIdEDataAgendamento(5L, data)).thenReturn(false);
        when(ordemServicoGateway.salvar(any(OrdemServico.class))).thenReturn(ordemSalva);
        when(servicoGateway.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> useCase.execute(data, BigDecimal.TEN, 5L, null, List.of(1L)));

        verify(ordemServicoEventGateway, never()).publicarOrdemServicoCriada(any(), any());
    }

    @Test
    @DisplayName("deve usar ordem salva quando detalhes nao forem encontrados")
    void execute_semDetalhes_deveUsarOrdemSalvaNoEvento() {
        LocalDateTime data = LocalDateTime.of(2026, 12, 1, 10, 0);
        OrdemServico ordemSalva = OrdemServico.builder().id(10L).dataAgendamento(data).precoMinimo(BigDecimal.TEN)
                .veiculo(Veiculo.builder().id(5L).placa("ABC1D23").build()).status(Status.builder().id(1L).build())
                .build();

        when(ordemServicoGateway.existePorVeiculoIdEDataAgendamento(5L, data)).thenReturn(false);
        when(ordemServicoGateway.salvar(any(OrdemServico.class))).thenReturn(ordemSalva);
        when(servicoGateway.buscarPorId(1L)).thenReturn(Optional.of(Servico.builder().id(1L).nome("Lavagem").build()));
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.empty());

        OrdemServico resultado = useCase.execute(data, BigDecimal.TEN, 5L, null, List.of(1L));

        assertEquals(10L, resultado.getId());
        ArgumentCaptor<List<String>> nomesCaptor = ArgumentCaptor.forClass(List.class);
        verify(ordemServicoEventGateway).publicarOrdemServicoCriada(any(OrdemServico.class), nomesCaptor.capture());
        assertEquals(List.of("Lavagem"), nomesCaptor.getValue());
    }
}
