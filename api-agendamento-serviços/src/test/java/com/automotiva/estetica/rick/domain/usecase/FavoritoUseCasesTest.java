package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.domain.entity.Favorito;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.AcessoNegadoException;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.FavoritoGateway;
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
class FavoritoUseCasesTest {

    @Mock
    private FavoritoGateway favoritoGateway;

    @Mock
    private PessoaGateway pessoaGateway;

    @Mock
    private ServicoGateway servicoGateway;

    @Mock
    private UsuarioAutenticadoGateway usuarioAutenticadoGateway;

    @InjectMocks
    private AdicionarFavoritoUseCase adicionarFavoritoUseCase;

    @InjectMocks
    private RemoverFavoritoUseCase removerFavoritoUseCase;

    @InjectMocks
    private ListarFavoritoPessoaUseCase listarFavoritoPessoaUseCase;

    @Test
    @DisplayName("Adicionar: deve lançar exceção quando usuário não existir")
    void adicionar_pessoaNaoEncontrada() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> adicionarFavoritoUseCase.execute(1L, 5L));
        verify(servicoGateway, never()).buscarPorId(anyLong());
    }

    @Test
    @DisplayName("Adicionar: deve lançar exceção quando serviço não existir")
    void adicionar_servicoNaoEncontrado() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(Pessoa.builder().id(1L).build()));
        when(servicoGateway.buscarPorId(5L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> adicionarFavoritoUseCase.execute(1L, 5L));
        verify(favoritoGateway, never()).salvar(any(Favorito.class));
    }

    @Test
    @DisplayName("Adicionar: deve lançar exceção quando usuário tentar operar favorito de outro dono")
    void adicionar_semOwnership() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(2L);

        assertThrows(AcessoNegadoException.class, () -> adicionarFavoritoUseCase.execute(1L, 5L));
        verify(pessoaGateway, never()).buscarPorId(anyLong());
    }

    @Test
    @DisplayName("Adicionar: deve lançar exceção quando favorito já existir")
    void adicionar_duplicado() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(Pessoa.builder().id(1L).build()));
        when(servicoGateway.buscarPorId(5L)).thenReturn(Optional.of(Servico.builder().id(5L).build()));
        when(favoritoGateway.existePorPessoaEServico(1L, 5L)).thenReturn(true);

        assertThrows(RecursoJaExisteException.class, () -> adicionarFavoritoUseCase.execute(1L, 5L));
        verify(favoritoGateway, never()).salvar(any(Favorito.class));
    }

    @Test
    @DisplayName("Adicionar: deve salvar favorito quando dados são válidos")
    void adicionar_sucesso() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(Pessoa.builder().id(1L).build()));
        when(servicoGateway.buscarPorId(5L)).thenReturn(Optional.of(Servico.builder().id(5L).build()));
        when(favoritoGateway.existePorPessoaEServico(1L, 5L)).thenReturn(false);

        adicionarFavoritoUseCase.execute(1L, 5L);

        verify(favoritoGateway).salvar(any(Favorito.class));
    }

    @Test
    @DisplayName("Remover: deve lançar exceção quando favorito não existir")
    void remover_naoEncontrado() {
        when(favoritoGateway.buscarPorId(9L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> removerFavoritoUseCase.execute(9L));
        verify(favoritoGateway, never()).deletarPorId(anyLong());
    }

    @Test
    @DisplayName("Remover: deve lançar exceção quando favorito pertence a outro usuário")
    void remover_semOwnership() {
        Favorito favorito = Favorito.builder().id(9L).pessoa(Pessoa.builder().id(1L).build()).build();
        when(favoritoGateway.buscarPorId(9L)).thenReturn(Optional.of(favorito));
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(2L);

        assertThrows(AcessoNegadoException.class, () -> removerFavoritoUseCase.execute(9L));
        verify(favoritoGateway, never()).deletarPorId(anyLong());
    }

    @Test
    @DisplayName("Remover: deve remover favorito quando ownership for válido")
    void remover_sucesso() {
        Favorito favorito = Favorito.builder().id(9L).pessoa(Pessoa.builder().id(1L).build()).build();
        when(favoritoGateway.buscarPorId(9L)).thenReturn(Optional.of(favorito));
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);

        removerFavoritoUseCase.execute(9L);

        verify(favoritoGateway).deletarPorId(9L);
    }

    @Test
    @DisplayName("Listar: deve retornar lista vazia quando pessoa não existir")
    void listar_pessoaNaoExiste() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.existePorId(1L)).thenReturn(false);

        List<Favorito> resultado = listarFavoritoPessoaUseCase.execute(1L);

        assertTrue(resultado.isEmpty());
        verify(favoritoGateway, never()).buscarPorPessoaId(anyLong());
    }

    @Test
    @DisplayName("Listar: deve lançar exceção quando ownership for inválido")
    void listar_semOwnership() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(2L);

        assertThrows(AcessoNegadoException.class, () -> listarFavoritoPessoaUseCase.execute(1L));
    }

    @Test
    @DisplayName("Listar: deve retornar favoritos quando pessoa existir")
    void listar_sucesso() {
        Favorito favorito = Favorito.builder().id(1L).build();
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(1L);
        when(pessoaGateway.existePorId(1L)).thenReturn(true);
        when(favoritoGateway.buscarPorPessoaId(1L)).thenReturn(List.of(favorito));

        List<Favorito> resultado = listarFavoritoPessoaUseCase.execute(1L);

        assertEquals(1, resultado.size());
        verify(favoritoGateway).buscarPorPessoaId(1L);
    }
}
