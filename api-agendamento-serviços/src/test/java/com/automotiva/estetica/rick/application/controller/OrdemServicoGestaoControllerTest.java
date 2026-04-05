package com.automotiva.estetica.rick.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.request.AdicionarServicosOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.AtualizarStatusOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.AtualizarValorServicoOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.OrdemServicoGestaoPageRequest;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoDetalheResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResumoResponse;
import com.automotiva.estetica.rick.application.service.OrdemServicoApplicationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de OrdemServicoGestaoController")
class OrdemServicoGestaoControllerTest {

    @Mock
    private OrdemServicoApplicationService ordemServicoUseCase;

    @InjectMocks
    private OrdemServicoGestaoController controller;

    @Test
    @DisplayName("buscarTodosParaGestao deve retornar pagina com status 200")
    void buscarTodosParaGestao_deveRetornarPaginaComStatus200() {
        OrdemServicoGestaoPageRequest request = OrdemServicoGestaoPageRequest.builder().status(1L)
                .dataInicio(LocalDate.now().minusDays(7)).dataFim(LocalDate.now()).build();
        Page<OrdemServicoResumoResponse> page = new PageImpl<>(List.of(OrdemServicoResumoResponse.builder().id(1L).build()));
        when(ordemServicoUseCase.buscarTodosParaGestao(request)).thenReturn(page);

        var response = controller.buscarTodosParaGestao(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(ordemServicoUseCase).buscarTodosParaGestao(request);
    }

    @Test
    @DisplayName("buscarDetalheParaGestao deve retornar detalhe com status 200")
    void buscarDetalheParaGestao_deveRetornarDetalheComStatus200() {
        OrdemServicoDetalheResponse detalhe = OrdemServicoDetalheResponse.builder().id(5L).build();
        when(ordemServicoUseCase.buscarDetalheParaGestao(5L)).thenReturn(detalhe);

        var response = controller.buscarDetalheParaGestao(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().getId());
        verify(ordemServicoUseCase).buscarDetalheParaGestao(5L);
    }

    @Test
    @DisplayName("atualizarStatusParaGestao deve delegar e retornar 200")
    void atualizarStatusParaGestao_deveDelegarERetornar200() {
        AtualizarStatusOrdemRequest request = AtualizarStatusOrdemRequest.builder().status(2L).build();
        OrdemServicoDetalheResponse detalhe = OrdemServicoDetalheResponse.builder().id(10L).build();
        when(ordemServicoUseCase.atualizarStatusParaGestao(10L, request)).thenReturn(detalhe);

        var response = controller.atualizarStatusParaGestao(10L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
        verify(ordemServicoUseCase).atualizarStatusParaGestao(10L, request);
    }

    @Test
    @DisplayName("adicionarServicosParaGestao deve retornar 201")
    void adicionarServicosParaGestao_deveRetornar201() {
        AdicionarServicosOrdemRequest request = AdicionarServicosOrdemRequest.builder().servicos(List.of()).build();
        OrdemServicoDetalheResponse detalhe = OrdemServicoDetalheResponse.builder().id(12L).build();
        when(ordemServicoUseCase.adicionarServicosParaGestao(12L, request)).thenReturn(detalhe);

        var response = controller.adicionarServicosParaGestao(12L, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(12L, response.getBody().getId());
        verify(ordemServicoUseCase).adicionarServicosParaGestao(12L, request);
    }

    @Test
    @DisplayName("atualizarValorServicoParaGestao deve delegar e retornar 200")
    void atualizarValorServicoParaGestao_deveDelegarERetornar200() {
        AtualizarValorServicoOrdemRequest request = AtualizarValorServicoOrdemRequest.builder()
                .valorAplicado(BigDecimal.TEN).build();
        OrdemServicoDetalheResponse detalhe = OrdemServicoDetalheResponse.builder().id(13L).build();
        when(ordemServicoUseCase.atualizarValorServicoParaGestao(13L, 8L, request)).thenReturn(detalhe);

        var response = controller.atualizarValorServicoParaGestao(13L, 8L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(13L, response.getBody().getId());
        verify(ordemServicoUseCase).atualizarValorServicoParaGestao(13L, 8L, request);
    }

    @Test
    @DisplayName("removerServicoParaGestao deve delegar e retornar 200")
    void removerServicoParaGestao_deveDelegarERetornar200() {
        OrdemServicoDetalheResponse detalhe = OrdemServicoDetalheResponse.builder().id(14L).build();
        when(ordemServicoUseCase.removerServicoParaGestao(14L, 3L)).thenReturn(detalhe);

        var response = controller.removerServicoParaGestao(14L, 3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(14L, response.getBody().getId());
        verify(ordemServicoUseCase).removerServicoParaGestao(14L, 3L);
    }
}


