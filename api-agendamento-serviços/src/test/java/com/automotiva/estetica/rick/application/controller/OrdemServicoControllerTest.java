package com.automotiva.estetica.rick.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.response.HorarioDisponivelResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResponse;
import com.automotiva.estetica.rick.application.security.OwnershipValidator;
import com.automotiva.estetica.rick.application.service.OrdemServicoApplicationService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de OrdemServicoController")
class OrdemServicoControllerTest {

    @Mock
    private OrdemServicoApplicationService ordemServicoUseCase;

    @Mock
    private OwnershipValidator ownershipValidator;

    @InjectMocks
    private OrdemServicoController ordemServicoController;

    @Test
    @DisplayName("buscarTodos deve retornar pagina com status 200")
    void buscarTodos_deveRetornarPaginaComStatus200() {
        PageRequest request = PageRequest.builder().pagina(0).tamanho(10).build();
        OrdemServicoResponse ordem = OrdemServicoResponse.builder().id(1L).build();
        Page<OrdemServicoResponse> page = new PageImpl<>(List.of(ordem));
        when(ordemServicoUseCase.buscarTodos(request)).thenReturn(page);

        var response = ordemServicoController.buscarTodos(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(ordemServicoUseCase).buscarTodos(request);
    }

    @Test
    @DisplayName("buscarPorId deve retornar ordem com status 200")
    void buscarPorId_deveRetornarOrdemComStatus200() {
        OrdemServicoResponse ordem = OrdemServicoResponse.builder().id(8L).build();
        when(ordemServicoUseCase.buscarPorId(8L)).thenReturn(ordem);

        var response = ordemServicoController.buscarPorId(8L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(8L, response.getBody().getId());
        verify(ordemServicoUseCase).buscarPorId(8L);
    }

    @Test
    @DisplayName("buscarPorUsuarioId deve validar ownership e retornar lista")
    void buscarPorUsuarioId_deveValidarOwnershipERetornarLista() {
        OrdemServicoResponse ordem = OrdemServicoResponse.builder().id(20L).build();
        when(ordemServicoUseCase.buscarPorUsuarioId(99L)).thenReturn(List.of(ordem));

        var response = ordemServicoController.buscarPorUsuarioId(99L);

        verify(ownershipValidator).validarPropriedade(99L);
        verify(ordemServicoUseCase).buscarPorUsuarioId(99L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("criar deve delegar para service e retornar 202")
    void criar_deveDelegarERetornar202() {
        OrdemServicoRequest request = OrdemServicoRequest.builder().veiculo(1L)
                .dataAgendamento(LocalDateTime.now().plusDays(1)).build();
        OrdemServicoResponse ordem = OrdemServicoResponse.builder().id(55L).build();
        when(ordemServicoUseCase.criar(request)).thenReturn(ordem);

        var response = ordemServicoController.criar(request);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(55L, response.getBody().getId());
        verify(ordemServicoUseCase).criar(request);
    }

    @Test
    @DisplayName("atualizar deve delegar para service e retornar 200")
    void atualizar_deveDelegarERetornar200() {
        OrdemServicoRequest request = OrdemServicoRequest.builder().observacoes("ok").build();
        OrdemServicoResponse ordem = OrdemServicoResponse.builder().id(77L).build();
        when(ordemServicoUseCase.atualizar(77L, request)).thenReturn(ordem);

        var response = ordemServicoController.atualizar(77L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(77L, response.getBody().getId());
        verify(ordemServicoUseCase).atualizar(77L, request);
    }

    @Test
    @DisplayName("buscarHorariosDisponiveis deve delegar para service e retornar 200")
    void buscarHorariosDisponiveis_deveDelegarERetornar200() {
        LocalDate data = LocalDate.of(2025, 12, 1);
        List<Long> servicosIds = List.of(1L, 2L);
        when(ordemServicoUseCase.buscarHorariosDisponiveis(data, servicosIds))
                .thenReturn(List.of(new HorarioDisponivelResponse(LocalTime.of(9, 0), LocalTime.of(10, 0))));

        var response = ordemServicoController.buscarHorariosDisponiveis(data, servicosIds);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(LocalTime.of(9, 0), response.getBody().getFirst().inicio());
        verify(ordemServicoUseCase).buscarHorariosDisponiveis(data, servicosIds);
    }
}


