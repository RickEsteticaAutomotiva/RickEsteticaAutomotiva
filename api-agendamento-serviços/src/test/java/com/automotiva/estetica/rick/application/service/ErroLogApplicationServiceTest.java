package com.automotiva.estetica.rick.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.assembler.ErroLogResponseAssembler;
import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.usecase.BuscarErroLogPorIdUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarErroLogsComFiltrosUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarErroLogsPaginadosUseCase;
import com.automotiva.estetica.rick.domain.usecase.PurgarErroLogsAntigosUseCase;
import com.automotiva.estetica.rick.domain.usecase.RegistrarErroLogUseCase;
import java.time.LocalDateTime;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
class ErroLogApplicationServiceTest {

    @Mock
    private RegistrarErroLogUseCase registrarErroLogUseCase;

    @Mock
    private BuscarErroLogPorIdUseCase buscarErroLogPorIdUseCase;

    @Mock
    private BuscarErroLogsPaginadosUseCase buscarErroLogsPaginadosUseCase;

    @Mock
    private BuscarErroLogsComFiltrosUseCase buscarErroLogsComFiltrosUseCase;

    @Mock
    private PurgarErroLogsAntigosUseCase purgarErroLogsAntigosUseCase;

    @Mock
    private ErroLogResponseAssembler erroLogResponseAssembler;

    @InjectMocks
    private ErroLogApplicationService erroLogApplicationService;

    @Test
    @DisplayName("Deve delegar registro de erro para use case")
    void registrar_deveDelegarParaUseCase() {
        ErroLog erroLog = ErroLog.builder().id(1L).mensagem("falha").build();

        erroLogApplicationService.registrar(erroLog);

        verify(registrarErroLogUseCase).execute(erroLog);
    }

    @Test
    @DisplayName("Nao deve propagar excecao ao falhar registro de erro")
    void registrar_quandoFalhar_naoDevePropagarExcecao() {
        ErroLog erroLog = ErroLog.builder().id(2L).build();
        doThrow(new RuntimeException("falha inesperada")).when(registrarErroLogUseCase).execute(erroLog);

        assertDoesNotThrow(() -> erroLogApplicationService.registrar(erroLog));
    }

    @Test
    @DisplayName("Deve delegar busca por id para use case e assembler")
    void buscarPorId_deveDelegarParaAssembler() {
        ErroLog erroLog = ErroLog.builder().id(15L).build();
        ErroLogResponse expected = ErroLogResponse.builder().id(15L).build();

        when(buscarErroLogPorIdUseCase.execute(15L)).thenReturn(erroLog);
        when(erroLogResponseAssembler.toRedactedResponse(erroLog)).thenReturn(expected);

        ErroLogResponse response = erroLogApplicationService.buscarPorId(15L);

        assertEquals(15L, response.getId());
        verify(buscarErroLogPorIdUseCase).execute(15L);
        verify(erroLogResponseAssembler).toRedactedResponse(erroLog);
    }

    @Test
    @DisplayName("Deve mapear pagina de logs usando assembler")
    void buscarTodos_deveMapearPagina() {
        ErroLog erroLog = ErroLog.builder().id(2L).build();
        ErroLogResponse dto = ErroLogResponse.builder().id(2L).build();
        var pageable = PageRequest.of(0, 10);
        Page<ErroLog> page = new PageImpl<>(java.util.List.of(erroLog), pageable, 1);

        when(buscarErroLogsPaginadosUseCase.execute(pageable)).thenReturn(page);
        when(erroLogResponseAssembler.toRedactedResponse(erroLog)).thenReturn(dto);

        Page<ErroLogResponse> response = erroLogApplicationService.buscarTodos(pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(2L, response.getContent().getFirst().getId());
        verify(erroLogResponseAssembler).toRedactedResponse(erroLog);
    }

    @Test
    @DisplayName("Deve mapear pagina com filtros usando assembler")
    void buscarComFiltros_deveMapearPagina() {
        ErroLog erroLog = ErroLog.builder().id(9L).build();
        ErroLogResponse dto = ErroLogResponse.builder().id(9L).build();
        LocalDateTime de = LocalDateTime.of(2026, 4, 1, 0, 0);
        LocalDateTime ate = LocalDateTime.of(2026, 4, 3, 23, 59);
        var pageable = PageRequest.of(0, 20);
        Page<ErroLog> page = new PageImpl<>(java.util.List.of(erroLog), pageable, 1);

        when(buscarErroLogsComFiltrosUseCase.execute("IllegalArgumentException", 400, "admin@rick.com", de, ate,
                pageable)).thenReturn(page);
        when(erroLogResponseAssembler.toRedactedResponse(erroLog)).thenReturn(dto);

        Page<ErroLogResponse> response = erroLogApplicationService.buscarComFiltros("IllegalArgumentException", 400,
                "admin@rick.com", de, ate, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(9L, response.getContent().getFirst().getId());
        verify(erroLogResponseAssembler).toRedactedResponse(erroLog);
    }

    @Test
    @DisplayName("Deve delegar purga para use case com data limite")
    void purgaLogAntigos_deveDelegarParaUseCase() {
        LocalDateTime antes = LocalDateTime.now();
        erroLogApplicationService.purgaLogAntigos();
        LocalDateTime depois = LocalDateTime.now();

        ArgumentCaptor<LocalDateTime> limiteCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(purgarErroLogsAntigosUseCase).execute(limiteCaptor.capture());

        LocalDateTime limite = limiteCaptor.getValue();
        assertTrue(!limite.isBefore(antes.minusDays(90)));
        assertTrue(!limite.isAfter(depois.minusDays(90)));
    }

    @Test
    @DisplayName("Deve delegar filtros nulos sem alterar parametros")
    void buscarComFiltros_parametrosNulos_deveDelegarNulos() {
        var pageable = PageRequest.of(0, 5);
        when(buscarErroLogsComFiltrosUseCase.execute(null, null, null, null, null, pageable))
                .thenReturn(Page.empty(pageable));

        Page<ErroLogResponse> response = erroLogApplicationService.buscarComFiltros(null, null, null, null, null,
                pageable);

        assertEquals(0, response.getTotalElements());
        verify(buscarErroLogsComFiltrosUseCase).execute(null, null, null, null, null, pageable);
    }
}
