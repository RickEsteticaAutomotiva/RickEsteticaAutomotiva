package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Status;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoEventGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AtualizarOrdemServicoUseCaseTest {

    @Mock
    private BuscarOrdemServicoComDetalhesUseCase buscarOrdemServicoComDetalhesUseCase;

    @Mock
    private OrdemServicoEventGateway ordemServicoEventGateway;

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @InjectMocks
    private AtualizarOrdemServicoUseCase useCase;

    @Test
    @DisplayName("deve atualizar ordem de servico e salvar com sucesso")
    void execute_deveAtualizarOrdemComSucesso() {
        Long ordemId = 10L;
        LocalDateTime novaData = LocalDateTime.of(2026, 12, 2, 14, 0);

        OrdemServico ordem = OrdemServico.builder().id(ordemId).dataAgendamento(LocalDateTime.of(2026, 12, 1, 10, 0))
                .precoMinimo(BigDecimal.TEN).observacoes("Obs antiga").veiculo(Veiculo.builder().id(5L).build())
                .status(Status.builder().id(1L).build()).build();

        when(buscarOrdemServicoComDetalhesUseCase.execute(ordemId)).thenReturn(ordem);
        when(ordemServicoGateway.salvar(ordem)).thenReturn(ordem);

        OrdemServico resultado = useCase.execute(ordemId, novaData, BigDecimal.valueOf(200), "Obs nova", 2L, 3L);

        assertEquals(ordemId, resultado.getId());
        verify(ordemServicoGateway).salvar(ordem);
        verify(ordemServicoEventGateway).publicarOrdemServicoAtualizada(ordem);
    }

    @Test
    @DisplayName("deve chamar atualizar com os novos dados")
    void execute_deveAplicarNovosValoresNaEntidade() {
        Long ordemId = 10L;
        LocalDateTime novaData = LocalDateTime.of(2026, 12, 2, 14, 0);

        OrdemServico ordem = OrdemServico.builder().id(ordemId).dataAgendamento(LocalDateTime.of(2026, 12, 1, 10, 0))
                .precoMinimo(BigDecimal.TEN).observacoes("Obs antiga").veiculo(Veiculo.builder().id(5L).build())
                .status(Status.builder().id(1L).build()).build();

        when(buscarOrdemServicoComDetalhesUseCase.execute(ordemId)).thenReturn(ordem);
        when(ordemServicoGateway.salvar(ordem)).thenReturn(ordem);

        useCase.execute(ordemId, novaData, BigDecimal.valueOf(200), "Obs nova", 2L, 3L);

        assertEquals(novaData, ordem.getDataAgendamento());
        assertEquals(BigDecimal.valueOf(200), ordem.getPrecoMinimo());
        assertEquals("Obs nova", ordem.getObservacoes());
    }

    @Test
    @DisplayName("deve publicar evento antes de salvar")
    void execute_devePublicarEventoAntesDeSalvar() {
        Long ordemId = 10L;

        OrdemServico ordem = OrdemServico.builder().id(ordemId).veiculo(Veiculo.builder().id(5L).build())
                .status(Status.builder().id(1L).build()).build();

        when(buscarOrdemServicoComDetalhesUseCase.execute(ordemId)).thenReturn(ordem);
        when(ordemServicoGateway.salvar(ordem)).thenReturn(ordem);

        useCase.execute(ordemId, LocalDateTime.now(), BigDecimal.TEN, "Obs", 2L, 3L);

        verify(ordemServicoEventGateway, times(1)).publicarOrdemServicoAtualizada(ordem);

        verify(ordemServicoGateway, times(1)).salvar(ordem);
    }

    @Test
    @DisplayName("deve propagar excecao quando ordem nao for encontrada")
    void execute_ordemNaoEncontrada_devePropagarExcecao() {
        Long ordemId = 10L;

        when(buscarOrdemServicoComDetalhesUseCase.execute(ordemId))
                .thenThrow(new RuntimeException("Ordem não encontrada"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> useCase.execute(ordemId, LocalDateTime.now(), BigDecimal.TEN, "Obs", 2L, 3L));

        verify(ordemServicoEventGateway, times(0)).publicarOrdemServicoAtualizada(org.mockito.Mockito.any());

        verify(ordemServicoGateway, times(0)).salvar(org.mockito.Mockito.any());
    }
}
