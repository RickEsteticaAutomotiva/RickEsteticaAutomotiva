package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.OrdemServicoDuracaoResumo;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarHorariosDisponiveisUseCaseTest {

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @InjectMocks
    private BuscarHorariosDisponiveisUseCase useCase;

    @Test
    @DisplayName("deve lancar quando lista de servicos nao for encontrada")
    void execute_servicosNaoEncontrados_deveLancar() {
        when(servicoGateway.buscarPorIds(List.of(1L))).thenReturn(List.of());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> useCase.execute(LocalDate.of(2025, 12, 1), List.of(1L)));
    }

    @Test
    @DisplayName("deve calcular horarios livres antes e depois das ordens do dia")
    void execute_comOrdensNoDia_deveRetornarSlotsDisponiveis() {
        LocalDate data = LocalDate.of(2025, 12, 1);

        Servico servico = Servico.builder().id(1L).duracaoMinutos(60).build();
        when(servicoGateway.buscarPorIds(List.of(1L))).thenReturn(List.of(servico));

        when(ordemServicoGateway.buscarDuracaoTotalPorOS(data))
                .thenReturn(List.of(new OrdemServicoDuracaoResumo(1L, LocalDateTime.of(2025, 12, 1, 10, 0), 180L)));

        var resultado = useCase.execute(data, List.of(1L));

        assertEquals(4, resultado.size());
        assertEquals(LocalTime.of(9, 0), resultado.getFirst().inicio());
        assertEquals(LocalTime.of(10, 0), resultado.getFirst().fim());
        assertEquals(LocalTime.of(13, 10), resultado.get(1).inicio());
        assertEquals(LocalTime.of(14, 10), resultado.get(1).fim());
        assertEquals(LocalTime.of(14, 20), resultado.get(2).inicio());
        assertEquals(LocalTime.of(15, 20), resultado.get(2).fim());
        assertEquals(LocalTime.of(15, 30), resultado.get(3).inicio());
        assertEquals(LocalTime.of(16, 30), resultado.get(3).fim());
    }
}
