package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.*;
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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BuscarHorariosDisponiveisUseCaseTest {

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @InjectMocks
    private BuscarHorariosDisponiveisUseCase useCase;

    private MockedStatic<LocalDate> mockedDate;

    @BeforeEach
    void setup() {
        LocalDate date = LocalDate.of(2025, 11, 30);

        mockedDate = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS);
        mockedDate.when(LocalDate::now).thenReturn(date);
    }

    @AfterEach
    void tearDown() {
        mockedDate.close();
    }

    @Test
    @DisplayName("deve lancar quando lista de servicos nao for encontrada")
    void execute_servicosNaoEncontrados_deveLancar() {
        LocalDate data = LocalDate.of(2026, 4, 23);
        when(servicoGateway.buscarPorIds(List.of(1L))).thenReturn(List.of());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(data, List.of(1L)));
    }

    @Test
    @DisplayName("deve calcular horarios livres antes e depois das ordens do dia")
    void execute_comOrdensNoDia_deveRetornarSlotsDisponiveis() {
        LocalDate data = LocalDate.of(2026, 4, 23);
        LocalDateTime dataHora = LocalDateTime.of(2026, 4, 23, 10, 0);
        Servico servico = Servico.builder().id(1L).duracaoMinutos(60).build();
        OrdemServicoDuracaoResumo resumo = new OrdemServicoDuracaoResumo(1L, dataHora, 180L);

        when(servicoGateway.buscarPorIds(List.of(1L))).thenReturn(List.of(servico));

        when(ordemServicoGateway.buscarDuracaoTotalPorOS(data)).thenReturn(List.of(resumo));

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

    @Test
    @DisplayName("deve retornar uma lista vazia quando o horário do dia atual for posterior ao final do horário de trabalho")
    void execute_deveRetornarListaVaziaHorarioPosterior() {
        try (MockedStatic<LocalTime> mock = Mockito.mockStatic(LocalTime.class, Mockito.CALLS_REAL_METHODS)) {
            LocalTime horarioMockado = LocalTime.of(23, 0);
            LocalDate data = LocalDate.of(2025, 11, 30);

            mock.when(LocalTime::now).thenReturn(horarioMockado);

            var resultado = useCase.execute(data, List.of(1L));

            assertTrue(resultado.isEmpty());
        }
    }
}
