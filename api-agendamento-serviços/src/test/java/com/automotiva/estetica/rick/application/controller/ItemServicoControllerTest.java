package com.automotiva.estetica.rick.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.usecase.BuscarItemServicoPorIdUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarItensServicoPorOrdemUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarItensServicoUseCase;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ItemServicoController")
class ItemServicoControllerTest {

    @Mock
    private ListarItensServicoUseCase listarItensServicoUseCase;

    @Mock
    private BuscarItemServicoPorIdUseCase buscarItemServicoPorIdUseCase;

    @Mock
    private ListarItensServicoPorOrdemUseCase listarItensServicoPorOrdemUseCase;

    @InjectMocks
    private ItemServicoController itemServicoController;

    @Test
    @DisplayName("buscarTodos deve mapear ids e retornar 200")
    void buscarTodos_deveMapearIdsERetornar200() {
        ItemServico item = ItemServico.builder().id(1L).servico(Servico.builder().id(10L).build())
                .ordemServico(OrdemServico.builder().id(100L).build()).preco(new BigDecimal("90.00")).build();
        when(listarItensServicoUseCase.execute()).thenReturn(List.of(item));

        var response = itemServicoController.buscarTodos();

        verify(listarItensServicoUseCase).execute();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(10L, response.getBody().getFirst().getIdServico());
        assertEquals(100L, response.getBody().getFirst().getIdOrdemServico());
    }

    @Test
    @DisplayName("buscarPorId deve mapear nulls quando servico e ordem estiverem ausentes")
    void buscarPorId_deveMapearNullsERetornar200() {
        ItemServico item = ItemServico.builder().id(2L).servico(null).ordemServico(null).preco(new BigDecimal("50.00"))
                .build();
        when(buscarItemServicoPorIdUseCase.execute(2L)).thenReturn(item);

        var response = itemServicoController.buscarPorId(2L);

        verify(buscarItemServicoPorIdUseCase).execute(2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        assertNull(response.getBody().getIdServico());
        assertNull(response.getBody().getIdOrdemServico());
    }

    @Test
    @DisplayName("listarPorOrdem deve delegar e retornar 200")
    void listarPorOrdem_deveDelegarERetornar200() {
        ItemServico item = ItemServico.builder().id(3L).servico(Servico.builder().id(30L).build())
                .ordemServico(OrdemServico.builder().id(300L).build()).preco(new BigDecimal("120.00")).build();
        when(listarItensServicoPorOrdemUseCase.execute(300L)).thenReturn(List.of(item));

        var response = itemServicoController.listarPorOrdem(300L);

        verify(listarItensServicoPorOrdemUseCase).execute(300L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(300L, response.getBody().getFirst().getIdOrdemServico());
    }
}
