package com.automotiva.estetica.rick.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.request.VeiculoRequest;
import com.automotiva.estetica.rick.application.dto.response.VeiculoResponse;
import com.automotiva.estetica.rick.application.mapper.VeiculoDTOMapper;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.usecase.AtualizarVeiculoUseCase;
import com.automotiva.estetica.rick.domain.usecase.CadastrarVeiculoUseCase;
import com.automotiva.estetica.rick.domain.usecase.DeletarVeiculoUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarVeiculosPorPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarVeiculosUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de VeiculoController")
class VeiculoControllerTest {

    @Mock
    private CadastrarVeiculoUseCase cadastrarVeiculoUseCase;

    @Mock
    private ListarVeiculosUseCase listarVeiculosUseCase;

    @Mock
    private ListarVeiculosPorPessoaUseCase listarVeiculosPorPessoaUseCase;

    @Mock
    private AtualizarVeiculoUseCase atualizarVeiculoUseCase;

    @Mock
    private DeletarVeiculoUseCase deletarVeiculoUseCase;

    @Mock
    private VeiculoDTOMapper veiculoDTOMapper;

    @InjectMocks
    private VeiculoController veiculoController;

    @Test
    @DisplayName("buscarTodos deve mapear veiculos e retornar 200")
    void buscarTodos_deveMapearVeiculosERetornar200() {
        Veiculo veiculo = Veiculo.builder().id(1L).placa("ABC1234").build();
        VeiculoResponse response = VeiculoResponse.builder().id(1L).placa("ABC1234").build();
        when(listarVeiculosUseCase.execute()).thenReturn(List.of(veiculo));
        when(veiculoDTOMapper.toResponse(veiculo)).thenReturn(response);

        var httpResponse = veiculoController.buscarTodos();

        assertEquals(HttpStatus.OK, httpResponse.getStatusCode());
        assertNotNull(httpResponse.getBody());
        assertEquals(1, httpResponse.getBody().size());
        assertEquals("ABC1234", httpResponse.getBody().getFirst().getPlaca());
        verify(listarVeiculosUseCase).execute();
    }

    @Test
    @DisplayName("buscarPorPessoa deve delegar use case e retornar 200")
    void buscarPorPessoa_deveDelegarERetornar200() {
        Veiculo veiculo = Veiculo.builder().id(2L).placa("DEF5678").build();
        VeiculoResponse response = VeiculoResponse.builder().id(2L).placa("DEF5678").build();
        when(listarVeiculosPorPessoaUseCase.execute(99L)).thenReturn(List.of(veiculo));
        when(veiculoDTOMapper.toResponse(veiculo)).thenReturn(response);

        var httpResponse = veiculoController.buscarPorPessoa(99L);

        assertEquals(HttpStatus.OK, httpResponse.getStatusCode());
        assertNotNull(httpResponse.getBody());
        assertEquals(1, httpResponse.getBody().size());
        verify(listarVeiculosPorPessoaUseCase).execute(99L);
    }

    @Test
    @DisplayName("cadastrar deve preencher idPessoa no dominio e retornar 201")
    void cadastrar_devePreencherPessoaERetornar201() {
        VeiculoRequest request = VeiculoRequest.builder().idPessoa(7L).placa("GHI9012").modelo("Civic").build();
        Veiculo dominio = Veiculo.builder().placa("GHI9012").modelo("Civic").build();
        Veiculo criado = Veiculo.builder().id(10L).placa("GHI9012").modelo("Civic").build();
        VeiculoResponse response = VeiculoResponse.builder().id(10L).placa("GHI9012").build();

        when(veiculoDTOMapper.toDomain(request)).thenReturn(dominio);
        when(cadastrarVeiculoUseCase.execute(dominio)).thenReturn(criado);
        when(veiculoDTOMapper.toResponse(criado)).thenReturn(response);

        var httpResponse = veiculoController.cadastrar(request);

        ArgumentCaptor<Veiculo> captor = ArgumentCaptor.forClass(Veiculo.class);
        verify(cadastrarVeiculoUseCase).execute(captor.capture());
        assertEquals(7L, captor.getValue().getPessoa().getId());
        assertEquals(HttpStatus.CREATED, httpResponse.getStatusCode());
        assertNotNull(httpResponse.getBody());
        assertEquals(10L, httpResponse.getBody().getId());
    }

    @Test
    @DisplayName("atualizar deve delegar campos e retornar 204")
    void atualizar_deveDelegarCamposERetornar204() {
        VeiculoRequest request = VeiculoRequest.builder().placa("AAA1111").modelo("HB20").marca("Hyundai")
                .porte("Hatch").cor("Preto").ano("2024").build();

        var httpResponse = veiculoController.atualizar(15L, request);

        verify(atualizarVeiculoUseCase).execute(15L, "AAA1111", "HB20", "Hyundai", "Hatch", "Preto", "2024");
        assertEquals(HttpStatus.NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    @DisplayName("deletar deve delegar id e retornar 204")
    void deletar_deveDelegarIdERetornar204() {
        var httpResponse = veiculoController.deletar(33L);

        verify(deletarVeiculoUseCase).execute(33L);
        assertEquals(HttpStatus.NO_CONTENT, httpResponse.getStatusCode());
    }
}
