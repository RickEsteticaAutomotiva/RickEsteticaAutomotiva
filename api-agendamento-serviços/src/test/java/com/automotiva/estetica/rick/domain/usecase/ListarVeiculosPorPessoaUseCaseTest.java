package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ListarVeiculosPorPessoaUseCase")
class ListarVeiculosPorPessoaUseCaseTest {

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private PessoaGateway pessoaGateway;

    @Test
    @DisplayName("deve retornar lista vazia quando pessoa nao existir")
    void execute_deveRetornarListaVaziaQuandoPessoaNaoExistir() {
        ListarVeiculosPorPessoaUseCase useCase = new ListarVeiculosPorPessoaUseCase(veiculoGateway, pessoaGateway);
        when(pessoaGateway.existePorId(10L)).thenReturn(false);

        List<Veiculo> resultado = useCase.execute(10L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("deve delegar ao gateway de veiculo quando pessoa existir")
    void execute_deveDelegarQuandoPessoaExistir() {
        ListarVeiculosPorPessoaUseCase useCase = new ListarVeiculosPorPessoaUseCase(veiculoGateway, pessoaGateway);
        List<Veiculo> esperado = List.of(Veiculo.builder().id(1L).placa("ABC1D23").build());
        when(pessoaGateway.existePorId(10L)).thenReturn(true);
        when(veiculoGateway.buscarPorPessoaId(10L)).thenReturn(esperado);

        List<Veiculo> resultado = useCase.execute(10L);

        assertEquals(1, resultado.size());
        assertEquals("ABC1D23", resultado.getFirst().getPlaca());
        verify(veiculoGateway).buscarPorPessoaId(10L);
    }
}
