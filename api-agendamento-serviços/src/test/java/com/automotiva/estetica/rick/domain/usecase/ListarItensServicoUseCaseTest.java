package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ListarItensServicoUseCase")
class ListarItensServicoUseCaseTest {

    @Mock
    private ItemServicoGateway itemServicoGateway;

    @InjectMocks
    private ListarItensServicoUseCase useCase;

    @Test
    @DisplayName("execute deve retornar itens quando houver registros")
    void execute_quandoHaItens_deveRetornarLista() {
        ItemServico item = ItemServico.builder().id(1L).build();
        when(itemServicoGateway.buscarTodos()).thenReturn(List.of(item));

        List<ItemServico> resultado = useCase.execute();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.getFirst().getId());
    }

    @Test
    @DisplayName("execute deve lancar RecursoNaoEncontradoException quando lista estiver vazia")
    void execute_quandoListaVazia_deveLancarRecursoNaoEncontrado() {
        when(itemServicoGateway.buscarTodos()).thenReturn(List.of());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute());
    }
}
