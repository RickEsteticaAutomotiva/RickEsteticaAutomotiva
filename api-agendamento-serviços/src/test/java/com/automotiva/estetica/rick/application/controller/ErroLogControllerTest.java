package com.automotiva.estetica.rick.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import com.automotiva.estetica.rick.application.service.ErroLogApplicationService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ErroLogController")
class ErroLogControllerTest {

    @Mock
    private ErroLogApplicationService erroLogUseCase;

    @InjectMocks
    private ErroLogController erroLogController;

    @Test
    @DisplayName("buscarTodos deve delegar com pageable e retornar 200")
    void buscarTodos_deveDelegarERetornar200() {
        PageRequest pageable = PageRequest.of(0, 20);
        Page<ErroLogResponse> esperado = new PageImpl<>(List.of(ErroLogResponse.builder().id(1L).build()));
        when(erroLogUseCase.buscarTodos(pageable)).thenReturn(esperado);

        var response = erroLogController.buscarTodos(pageable);

        verify(erroLogUseCase).buscarTodos(pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }

    @Test
    @DisplayName("buscarPorId deve delegar com id e retornar 200")
    void buscarPorId_deveDelegarERetornar200() {
        ErroLogResponse esperado = ErroLogResponse.builder().id(99L).tipoExcecao("NullPointerException").build();
        when(erroLogUseCase.buscarPorId(99L)).thenReturn(esperado);

        var response = erroLogController.buscarPorId(99L);

        verify(erroLogUseCase).buscarPorId(99L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }

    @Test
    @DisplayName("buscarComFiltros deve delegar todos os parametros e retornar 200")
    void buscarComFiltros_deveDelegarParametrosERetornar200() {
        String tipoExcecao = "IllegalArgumentException";
        Integer statusHttp = 400;
        String usuarioEmail = "admin@rick.com";
        LocalDateTime de = LocalDateTime.of(2026, 4, 1, 0, 0);
        LocalDateTime ate = LocalDateTime.of(2026, 4, 3, 23, 59);
        PageRequest pageable = PageRequest.of(1, 10);
        Page<ErroLogResponse> esperado = new PageImpl<>(List.of(ErroLogResponse.builder().id(5L).build()));
        when(erroLogUseCase.buscarComFiltros(tipoExcecao, statusHttp, usuarioEmail, de, ate, pageable))
                .thenReturn(esperado);

        var response = erroLogController.buscarComFiltros(tipoExcecao, statusHttp, usuarioEmail, de, ate, pageable);

        verify(erroLogUseCase).buscarComFiltros(tipoExcecao, statusHttp, usuarioEmail, de, ate, pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(esperado, response.getBody());
    }
}

