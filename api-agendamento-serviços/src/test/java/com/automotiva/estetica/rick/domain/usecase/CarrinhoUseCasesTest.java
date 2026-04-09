package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.domain.entity.Carrinho;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.AcessoNegadoException;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.CarrinhoGateway;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.UsuarioAutenticadoGateway;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarrinhoUseCasesTest {

    @Mock
    private CarrinhoGateway carrinhoGateway;

    @Mock
    private PessoaGateway pessoaGateway;

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private UsuarioAutenticadoGateway usuarioAutenticadoGateway;

    @InjectMocks
    private AdicionarCarrinhoUseCase adicionarCarrinhoUseCase;

    @InjectMocks
    private RemoverCarrinhoUseCase removerCarrinhoUseCase;

    @InjectMocks
    private LimparCarrinhoPessoaUseCase limparCarrinhoPessoaUseCase;

    @InjectMocks
    private ListarCarrinhoPessoaUseCase listarCarrinhoPessoaUseCase;

    @Test
    @DisplayName("Adicionar: deve lançar exceção quando usuário não existir")
    void adicionar_pessoaNaoEncontrada() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> adicionarCarrinhoUseCase.execute(1L, 10L));
        verify(servicoGateway, never()).buscarPorId(anyLong());
    }

    @Test
    @DisplayName("Adicionar: deve lançar exceção quando serviço não existir")
    void adicionar_servicoNaoEncontrado() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(Pessoa.builder().id(1L).build()));
        when(servicoGateway.buscarPorId(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> adicionarCarrinhoUseCase.execute(1L, 10L));
        verify(carrinhoGateway, never()).salvar(any(Carrinho.class));
    }

    @Test
    @DisplayName("Adicionar: deve lançar exceção quando usuário tentar operar carrinho de outro dono")
    void adicionar_semOwnership() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(2L);

        assertThrows(AcessoNegadoException.class, () -> adicionarCarrinhoUseCase.execute(1L, 10L));
        verify(pessoaGateway, never()).buscarPorId(anyLong());
    }

    @Test
    @DisplayName("Adicionar: deve lançar exceção quando serviço já estiver no carrinho")
    void adicionar_itemDuplicado() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(Pessoa.builder().id(1L).build()));
        when(servicoGateway.buscarPorId(10L)).thenReturn(Optional.of(Servico.builder().id(10L).build()));
        when(carrinhoGateway.existePorPessoaEServico(1L, 10L)).thenReturn(true);

        assertThrows(RecursoJaExisteException.class, () -> adicionarCarrinhoUseCase.execute(1L, 10L));
        verify(carrinhoGateway, never()).salvar(any(Carrinho.class));
    }

    @Test
    @DisplayName("Adicionar: deve salvar item quando dados são válidos")
    void adicionar_sucesso() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(Pessoa.builder().id(1L).build()));
        when(servicoGateway.buscarPorId(10L)).thenReturn(Optional.of(Servico.builder().id(10L).build()));
        when(carrinhoGateway.existePorPessoaEServico(1L, 10L)).thenReturn(false);

        adicionarCarrinhoUseCase.execute(1L, 10L);

        verify(carrinhoGateway).salvar(any(Carrinho.class));
    }

    @Test
    @DisplayName("Remover: deve lançar exceção quando item não existe")
    void remover_itemNaoEncontrado() {
        when(carrinhoGateway.buscarPorId(9L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> removerCarrinhoUseCase.execute(9L));
        verify(carrinhoGateway, never()).deletarPorId(anyLong());
    }

    @Test
    @DisplayName("Remover: deve lançar exceção quando item pertence a outro usuário")
    void remover_semOwnership() {
        Carrinho carrinho = Carrinho.builder().id(9L).pessoa(Pessoa.builder().id(1L).build()).build();
        when(carrinhoGateway.buscarPorId(9L)).thenReturn(Optional.of(carrinho));
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(2L);

        assertThrows(AcessoNegadoException.class, () -> removerCarrinhoUseCase.execute(9L));
        verify(carrinhoGateway, never()).deletarPorId(anyLong());
    }

    @Test
    @DisplayName("Remover: deve remover item quando ownership for válido")
    void remover_sucesso() {
        Carrinho carrinho = Carrinho.builder().id(9L).pessoa(Pessoa.builder().id(1L).build()).build();
        when(carrinhoGateway.buscarPorId(9L)).thenReturn(Optional.of(carrinho));
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);

        removerCarrinhoUseCase.execute(9L);

        verify(carrinhoGateway).deletarPorId(9L);
    }

    @Test
    @DisplayName("Limpar: deve deletar todos os itens quando carrinho possui itens")
    void limpar_comItens() {
        Carrinho item = Carrinho.builder().id(1L).build();

        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(Pessoa.builder().id(1L).build()));
        when(carrinhoGateway.buscarPorPessoaId(1L)).thenReturn(List.of(item));

        limparCarrinhoPessoaUseCase.execute(1L);

        verify(carrinhoGateway).deletarTodos(List.of(item));
    }

    @Test
    @DisplayName("Limpar: não deve deletar quando carrinho estiver vazio")
    void limpar_semItens() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(Pessoa.builder().id(1L).build()));
        when(carrinhoGateway.buscarPorPessoaId(1L)).thenReturn(List.of());

        limparCarrinhoPessoaUseCase.execute(1L);

        verify(carrinhoGateway, never()).deletarTodos(anyList());
    }

    @Test
    @DisplayName("Limpar: não deve deletar quando gateway retornar lista nula")
    void limpar_itensNulos() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(Pessoa.builder().id(1L).build()));
        when(carrinhoGateway.buscarPorPessoaId(1L)).thenReturn(null);

        limparCarrinhoPessoaUseCase.execute(1L);

        verify(carrinhoGateway, never()).deletarTodos(anyList());
    }

    @Test
    @DisplayName("Limpar: deve lançar exceção quando ownership for inválido")
    void limpar_semOwnership() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(2L);

        assertThrows(AcessoNegadoException.class, () -> limparCarrinhoPessoaUseCase.execute(1L));
        verify(pessoaGateway, never()).buscarPorId(anyLong());
    }

    @Test
    @DisplayName("Limpar: deve lançar exceção quando pessoa não existir")
    void limpar_pessoaNaoEncontrada() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> limparCarrinhoPessoaUseCase.execute(1L));
    }

    @Test
    @DisplayName("Listar: deve retornar lista vazia quando usuário não existe")
    void listar_pessoaNaoExiste() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.existePorId(1L)).thenReturn(false);

        List<Carrinho> resultado = listarCarrinhoPessoaUseCase.execute(1L);

        assertTrue(resultado.isEmpty());
        verify(carrinhoGateway, never()).buscarPorPessoaId(anyLong());
    }

    @Test
    @DisplayName("Listar: deve lançar exceção quando ownership for inválido")
    void listar_semOwnership() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(2L);

        assertThrows(AcessoNegadoException.class, () -> listarCarrinhoPessoaUseCase.execute(1L));
    }

    @Test
    @DisplayName("Listar: deve retornar itens quando pessoa existir")
    void listar_sucesso() {
        Carrinho item = Carrinho.builder().id(1L).build();
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.existePorId(1L)).thenReturn(true);
        when(carrinhoGateway.buscarPorPessoaId(1L)).thenReturn(List.of(item));

        List<Carrinho> resultado = listarCarrinhoPessoaUseCase.execute(1L);

        assertEquals(1, resultado.size());
        verify(carrinhoGateway).buscarPorPessoaId(1L);
    }
}
