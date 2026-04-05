package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.CategoriaGateway;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de delegacao de use cases CRUD")
class CrudDelegationUseCasesTest {

    @Mock
    private PessoaGateway pessoaGateway;

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private CategoriaGateway categoriaGateway;

    @Test
    @DisplayName("ListarPessoasUseCase deve delegar para gateway")
    void listarPessoas_deveDelegar() {
        ListarPessoasUseCase useCase = new ListarPessoasUseCase(pessoaGateway);
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Pessoa> esperado = new PageImpl<>(List.of(Pessoa.builder().id(1L).build()));
        when(pessoaGateway.buscarTodos("ana", pageable)).thenReturn(esperado);

        Page<Pessoa> resultado = useCase.execute("ana", pageable);

        assertEquals(esperado, resultado);
        verify(pessoaGateway).buscarTodos("ana", pageable);
    }

    @Test
    @DisplayName("BuscarPessoaPorIdUseCase deve retornar entidade quando existir")
    void buscarPessoaPorId_deveRetornarQuandoExistir() {
        BuscarPessoaPorIdUseCase useCase = new BuscarPessoaPorIdUseCase(pessoaGateway);
        Pessoa esperado = Pessoa.builder().id(9L).build();
        when(pessoaGateway.buscarPorId(9L)).thenReturn(Optional.of(esperado));

        Pessoa resultado = useCase.execute(9L);

        assertEquals(esperado, resultado);
    }

    @Test
    @DisplayName("BuscarPessoaPorIdUseCase deve lancar quando nao existir")
    void buscarPessoaPorId_deveLancarQuandoNaoExistir() {
        BuscarPessoaPorIdUseCase useCase = new BuscarPessoaPorIdUseCase(pessoaGateway);
        when(pessoaGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(99L));
    }

    @Test
    @DisplayName("DeletarPessoaUseCase deve deletar quando id existir")
    void deletarPessoa_deveDeletarQuandoExistir() {
        DeletarPessoaUseCase useCase = new DeletarPessoaUseCase(pessoaGateway);
        when(pessoaGateway.existePorId(5L)).thenReturn(true);

        useCase.execute(5L);

        verify(pessoaGateway).deletarPorId(5L);
    }

    @Test
    @DisplayName("DeletarPessoaUseCase deve lancar quando id nao existir")
    void deletarPessoa_deveLancarQuandoNaoExistir() {
        DeletarPessoaUseCase useCase = new DeletarPessoaUseCase(pessoaGateway);
        when(pessoaGateway.existePorId(50L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(50L));
        verify(pessoaGateway, never()).deletarPorId(50L);
    }

    @Test
    @DisplayName("ListarServicosUseCase deve delegar para gateway")
    void listarServicos_deveDelegar() {
        ListarServicosUseCase useCase = new ListarServicosUseCase(servicoGateway);
        PageRequest pageable = PageRequest.of(1, 5);
        Page<Servico> esperado = new PageImpl<>(List.of(Servico.builder().id(2L).build()));
        when(servicoGateway.buscarTodos("lavagem", pageable)).thenReturn(esperado);

        Page<Servico> resultado = useCase.execute("lavagem", pageable);

        assertEquals(esperado, resultado);
        verify(servicoGateway).buscarTodos("lavagem", pageable);
    }

    @Test
    @DisplayName("BuscarServicoPorIdUseCase deve retornar entidade quando existir")
    void buscarServicoPorId_deveRetornarQuandoExistir() {
        BuscarServicoPorIdUseCase useCase = new BuscarServicoPorIdUseCase(servicoGateway);
        Servico esperado = Servico.builder().id(4L).build();
        when(servicoGateway.buscarPorId(4L)).thenReturn(Optional.of(esperado));

        Servico resultado = useCase.execute(4L);

        assertEquals(esperado, resultado);
    }

    @Test
    @DisplayName("BuscarServicoPorIdUseCase deve lancar quando nao existir")
    void buscarServicoPorId_deveLancarQuandoNaoExistir() {
        BuscarServicoPorIdUseCase useCase = new BuscarServicoPorIdUseCase(servicoGateway);
        when(servicoGateway.buscarPorId(44L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(44L));
    }

    @Test
    @DisplayName("DeletarServicoUseCase deve deletar quando id existir")
    void deletarServico_deveDeletarQuandoExistir() {
        DeletarServicoUseCase useCase = new DeletarServicoUseCase(servicoGateway);
        when(servicoGateway.existePorId(7L)).thenReturn(true);

        useCase.execute(7L);

        verify(servicoGateway).deletarPorId(7L);
    }

    @Test
    @DisplayName("DeletarServicoUseCase deve lancar quando id nao existir")
    void deletarServico_deveLancarQuandoNaoExistir() {
        DeletarServicoUseCase useCase = new DeletarServicoUseCase(servicoGateway);
        when(servicoGateway.existePorId(70L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(70L));
        verify(servicoGateway, never()).deletarPorId(70L);
    }

    @Test
    @DisplayName("ListarVeiculosUseCase deve delegar para gateway")
    void listarVeiculos_deveDelegar() {
        ListarVeiculosUseCase useCase = new ListarVeiculosUseCase(veiculoGateway);
        List<Veiculo> esperado = List.of(Veiculo.builder().id(11L).build());
        when(veiculoGateway.buscarTodos()).thenReturn(esperado);

        List<Veiculo> resultado = useCase.execute();

        assertEquals(esperado, resultado);
        verify(veiculoGateway).buscarTodos();
    }

    @Test
    @DisplayName("BuscarVeiculoPorIdUseCase deve retornar entidade quando existir")
    void buscarVeiculoPorId_deveRetornarQuandoExistir() {
        BuscarVeiculoPorIdUseCase useCase = new BuscarVeiculoPorIdUseCase(veiculoGateway);
        Veiculo esperado = Veiculo.builder().id(12L).build();
        when(veiculoGateway.buscarPorId(12L)).thenReturn(Optional.of(esperado));

        Veiculo resultado = useCase.execute(12L);

        assertEquals(esperado, resultado);
    }

    @Test
    @DisplayName("BuscarVeiculoPorIdUseCase deve lancar quando nao existir")
    void buscarVeiculoPorId_deveLancarQuandoNaoExistir() {
        BuscarVeiculoPorIdUseCase useCase = new BuscarVeiculoPorIdUseCase(veiculoGateway);
        when(veiculoGateway.buscarPorId(120L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(120L));
    }

    @Test
    @DisplayName("DeletarVeiculoUseCase deve deletar quando id existir")
    void deletarVeiculo_deveDeletarQuandoExistir() {
        DeletarVeiculoUseCase useCase = new DeletarVeiculoUseCase(veiculoGateway);
        when(veiculoGateway.existePorId(13L)).thenReturn(true);

        useCase.execute(13L);

        verify(veiculoGateway).deletarPorId(13L);
    }

    @Test
    @DisplayName("DeletarVeiculoUseCase deve lancar quando id nao existir")
    void deletarVeiculo_deveLancarQuandoNaoExistir() {
        DeletarVeiculoUseCase useCase = new DeletarVeiculoUseCase(veiculoGateway);
        when(veiculoGateway.existePorId(130L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(130L));
        verify(veiculoGateway, never()).deletarPorId(130L);
    }

    @Test
    @DisplayName("ListarCategoriasUseCase deve delegar para gateway")
    void listarCategorias_deveDelegar() {
        ListarCategoriasUseCase useCase = new ListarCategoriasUseCase(categoriaGateway);
        List<Categoria> esperado = List.of(Categoria.builder().id(20L).build());
        when(categoriaGateway.buscarTodas()).thenReturn(esperado);

        List<Categoria> resultado = useCase.execute();

        assertEquals(esperado, resultado);
        verify(categoriaGateway).buscarTodas();
    }

    @Test
    @DisplayName("BuscarCategoriaPorIdUseCase deve retornar entidade quando existir")
    void buscarCategoriaPorId_deveRetornarQuandoExistir() {
        BuscarCategoriaPorIdUseCase useCase = new BuscarCategoriaPorIdUseCase(categoriaGateway);
        Categoria esperado = Categoria.builder().id(21L).build();
        when(categoriaGateway.buscarPorId(21L)).thenReturn(Optional.of(esperado));

        Categoria resultado = useCase.execute(21L);

        assertEquals(esperado, resultado);
    }

    @Test
    @DisplayName("BuscarCategoriaPorIdUseCase deve lancar quando nao existir")
    void buscarCategoriaPorId_deveLancarQuandoNaoExistir() {
        BuscarCategoriaPorIdUseCase useCase = new BuscarCategoriaPorIdUseCase(categoriaGateway);
        when(categoriaGateway.buscarPorId(210L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(210L));
    }

    @Test
    @DisplayName("DeletarCategoriaUseCase deve deletar quando id existir")
    void deletarCategoria_deveDeletarQuandoExistir() {
        DeletarCategoriaUseCase useCase = new DeletarCategoriaUseCase(categoriaGateway);
        when(categoriaGateway.existePorId(22L)).thenReturn(true);

        useCase.execute(22L);

        verify(categoriaGateway).deletarPorId(22L);
    }

    @Test
    @DisplayName("DeletarCategoriaUseCase deve lancar quando id nao existir")
    void deletarCategoria_deveLancarQuandoNaoExistir() {
        DeletarCategoriaUseCase useCase = new DeletarCategoriaUseCase(categoriaGateway);
        when(categoriaGateway.existePorId(220L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(220L));
        verify(categoriaGateway, never()).deletarPorId(220L);
    }

    @Test
    @DisplayName("AtualizarCategoriaUseCase deve atualizar nome quando valor for valido")
    void atualizarCategoria_deveAtualizarNomeQuandoValido() {
        AtualizarCategoriaUseCase useCase = new AtualizarCategoriaUseCase(categoriaGateway);
        Categoria categoria = Categoria.builder().id(30L).nome("Antes").build();
        when(categoriaGateway.buscarPorId(30L)).thenReturn(Optional.of(categoria));
        when(categoriaGateway.salvar(categoria)).thenReturn(categoria);

        Categoria resultado = useCase.execute(30L, "Depois");

        assertEquals("Depois", resultado.getNome());
        verify(categoriaGateway).salvar(categoria);
    }

    @Test
    @DisplayName("AtualizarCategoriaUseCase deve manter nome quando valor for blank")
    void atualizarCategoria_deveManterNomeQuandoBlank() {
        AtualizarCategoriaUseCase useCase = new AtualizarCategoriaUseCase(categoriaGateway);
        Categoria categoria = Categoria.builder().id(31L).nome("Original").build();
        when(categoriaGateway.buscarPorId(31L)).thenReturn(Optional.of(categoria));
        when(categoriaGateway.salvar(categoria)).thenReturn(categoria);

        Categoria resultado = useCase.execute(31L, "   ");

        assertEquals("Original", resultado.getNome());
        verify(categoriaGateway).salvar(categoria);
    }

    @Test
    @DisplayName("AtualizarCategoriaUseCase deve lançar quando categoria não existir")
    void atualizarCategoria_deveLancarQuandoNaoExistir() {
        AtualizarCategoriaUseCase useCase = new AtualizarCategoriaUseCase(categoriaGateway);
        when(categoriaGateway.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(999L, "Nome"));
        verify(categoriaGateway, never()).salvar(org.mockito.ArgumentMatchers.any(Categoria.class));
    }

    @Test
    @DisplayName("AtualizarServicoUseCase deve atualizar campos informados e salvar")
    void atualizarServico_deveAtualizarCamposInformados() {
        BuscarServicoPorIdUseCase buscarServicoPorIdUseCase = org.mockito.Mockito.mock(BuscarServicoPorIdUseCase.class);
        AtualizarServicoUseCase useCase = new AtualizarServicoUseCase(buscarServicoPorIdUseCase, servicoGateway);
        Servico servico = Servico.builder().id(40L).nome("Lavagem").descricao("desc")
                .preco(new BigDecimal("50.00")).build();
        when(buscarServicoPorIdUseCase.execute(40L)).thenReturn(servico);
        when(servicoGateway.salvar(servico)).thenReturn(servico);

        Servico resultado = useCase.execute(40L, "Polimento", "premium", new BigDecimal("120.00"), "img.png", 7L,
                LocalTime.of(2, 0));

        assertEquals("Polimento", resultado.getNome());
        assertEquals("premium", resultado.getDescricao());
        assertEquals(new BigDecimal("120.00"), resultado.getPreco());
        assertEquals(7L, resultado.getCategoria().getId());
        assertEquals(LocalTime.of(2, 0), resultado.getDuracaoHoras());
        verify(servicoGateway).salvar(servico);
    }

    @Test
    @DisplayName("AtualizarServicoUseCase deve manter campos quando parametros forem nulos")
    void atualizarServico_deveManterCamposQuandoNulos() {
        BuscarServicoPorIdUseCase buscarServicoPorIdUseCase = org.mockito.Mockito.mock(BuscarServicoPorIdUseCase.class);
        AtualizarServicoUseCase useCase = new AtualizarServicoUseCase(buscarServicoPorIdUseCase, servicoGateway);
        Servico servico = Servico.builder().id(41L).nome("Original").descricao("desc")
                .preco(new BigDecimal("70.00")).build();
        when(buscarServicoPorIdUseCase.execute(41L)).thenReturn(servico);
        when(servicoGateway.salvar(servico)).thenReturn(servico);

        Servico resultado = useCase.execute(41L, null, null, null, null, null, null);

        assertEquals("Original", resultado.getNome());
        assertEquals("desc", resultado.getDescricao());
        assertEquals(new BigDecimal("70.00"), resultado.getPreco());
        verify(servicoGateway).salvar(servico);
    }
}



