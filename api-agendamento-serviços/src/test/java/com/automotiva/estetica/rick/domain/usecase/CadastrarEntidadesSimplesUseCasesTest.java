package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.gateway.CategoriaGateway;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de cadastro de entidades simples")
class CadastrarEntidadesSimplesUseCasesTest {

    @Mock
    private CategoriaGateway categoriaGateway;

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private VeiculoGateway veiculoGateway;

    @Test
    @DisplayName("CadastrarCategoriaUseCase deve salvar e retornar categoria")
    void cadastrarCategoria_deveSalvarERetornar() {
        CadastrarCategoriaUseCase useCase = new CadastrarCategoriaUseCase(categoriaGateway);
        Categoria categoria = Categoria.builder().nome("Lavagem").build();
        when(categoriaGateway.salvar(categoria)).thenReturn(categoria);

        Categoria resultado = useCase.execute(categoria);

        assertEquals("Lavagem", resultado.getNome());
        verify(categoriaGateway).salvar(categoria);
    }

    @Test
    @DisplayName("CadastrarServicoUseCase deve salvar e retornar servico")
    void cadastrarServico_deveSalvarERetornar() {
        CadastrarServicoUseCase useCase = new CadastrarServicoUseCase(servicoGateway);
        Servico servico = Servico.builder().nome("Polimento").preco(new BigDecimal("100.00")).build();
        when(servicoGateway.salvar(servico)).thenReturn(servico);

        Servico resultado = useCase.execute(servico);

        assertEquals("Polimento", resultado.getNome());
        verify(servicoGateway).salvar(servico);
    }

    @Test
    @DisplayName("CadastrarVeiculoUseCase deve salvar e retornar veiculo")
    void cadastrarVeiculo_deveSalvarERetornar() {
        CadastrarVeiculoUseCase useCase = new CadastrarVeiculoUseCase(veiculoGateway);
        Veiculo veiculo = Veiculo.builder().placa("ABC1D23").build();
        when(veiculoGateway.salvar(veiculo)).thenReturn(veiculo);

        Veiculo resultado = useCase.execute(veiculo);

        assertEquals("ABC1D23", resultado.getPlaca());
        verify(veiculoGateway).salvar(veiculo);
    }

    @Test
    @DisplayName("CadastrarServicoUseCase deve propagar excecao do gateway")
    void cadastrarServico_devePropagarExcecaoDoGateway() {
        CadastrarServicoUseCase useCase = new CadastrarServicoUseCase(servicoGateway);
        Servico servico = Servico.builder().nome("Falho").build();
        when(servicoGateway.salvar(servico)).thenThrow(new RuntimeException("erro persistencia"));

        assertThrows(RuntimeException.class, () -> useCase.execute(servico));
    }
}
