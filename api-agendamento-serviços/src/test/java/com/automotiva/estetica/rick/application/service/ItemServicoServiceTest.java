package com.automotiva.estetica.rick.application.service;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.dto.response.ItemServicoResponse;
import com.automotiva.estetica.rick.application.port.out.ItemServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemServicoServiceTest {

    @Mock
    private ItemServicoRepositoryPort itemServicoRepositoryPort;

    @InjectMocks
    private ItemServicoService itemServicoService;

    private ItemServico itemMock() {
        Servico servico = Servico.builder().id(1L).build();
        OrdemServico ordem = OrdemServico.builder().id(10L).build();
        return ItemServico.builder().id(1L).servico(servico).ordemServico(ordem).preco(BigDecimal.valueOf(100)).build();
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar todos quando a lista estiver vazia")
    void buscarTodos_listaVazia_develancarExcecao() {
        when(itemServicoRepositoryPort.buscarTodos()).thenReturn(emptyList());

        assertThrows(RecursoNaoEncontradoException.class, () -> itemServicoService.buscarTodos());
    }

    @Test
    @DisplayName("Deve retornar lista de itens quando existirem elementos")
    void buscarTodos_sucesso() {
        when(itemServicoRepositoryPort.buscarTodos()).thenReturn(List.of(itemMock()));

        List<ItemServicoResponse> resposta = itemServicoService.buscarTodos();

        assertEquals(1, resposta.size());
        assertEquals(1L, resposta.getFirst().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar item inexistente por ID")
    void buscarPorId_inexistente_deveLancarExcecao() {
        when(itemServicoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> itemServicoService.buscarPorId(1L));
    }

    @Test
    @DisplayName("Deve retornar item buscado por ID com sucesso")
    void buscarPorId_sucesso() {
        when(itemServicoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(itemMock()));

        ItemServicoResponse resposta = itemServicoService.buscarPorId(1L);

        assertNotNull(resposta);
        assertEquals(1L, resposta.getId());
        assertEquals(1L, resposta.getIdServico());
        assertEquals(10L, resposta.getIdOrdemServico());
    }

    @Test
    @DisplayName("Deve retornar lista de itens ao listar por ordem de serviço")
    void listarPorOrdem_sucesso() {
        when(itemServicoRepositoryPort.buscarPorOrdemServicoId(10L))
                .thenReturn(List.of(itemMock()));

        List<ItemServicoResponse> resposta = itemServicoService.listarPorOrdem(10L);

        assertEquals(1, resposta.size());
    }
}
