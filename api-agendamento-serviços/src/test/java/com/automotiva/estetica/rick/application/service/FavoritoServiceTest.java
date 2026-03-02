package com.automotiva.estetica.rick.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.dto.request.FavoritoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoFavoritoResponse;
import com.automotiva.estetica.rick.application.port.out.FavoritoRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.PessoaRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.ServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Favorito;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
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
class FavoritoServiceTest {

    @Mock
    private FavoritoRepositoryPort favoritoRepositoryPort;

    @Mock
    private PessoaRepositoryPort pessoaRepositoryPort;

    @Mock
    private ServicoRepositoryPort servicoRepositoryPort;

    @InjectMocks
    private FavoritoService favoritoService;

    private Pessoa pessoaMock() {
        return Pessoa.builder().id(1L).nome("Maria").build();
    }

    private Servico servicoMock() {
        return Servico.builder()
                .id(5L)
                .nome("Lavagem")
                .descricao("Lavagem completa")
                .preco(BigDecimal.valueOf(80))
                .imagem("lavagem.jpg")
                .build();
    }

    private FavoritoRequest requestMock() {
        FavoritoRequest req = new FavoritoRequest();
        req.setIdPessoa(1L);
        req.setIdServico(5L);
        return req;
    }

    // ─── adicionar ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao adicionar favorito quando pessoa não existir")
    void adicionar_pessoaNaoEncontrada_deveLancarExcecao() {
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> favoritoService.adicionar(requestMock()));
        verify(servicoRepositoryPort, never()).buscarPorId(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar favorito quando serviço não existir")
    void adicionar_servicoNaoEncontrado_deveLancarExcecao() {
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoaMock()));
        when(servicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> favoritoService.adicionar(requestMock()));
        verify(favoritoRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar favorito duplicado")
    void adicionar_favoritoDuplicado_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();
        Servico servico = servicoMock();

        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(servicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(servico));
        when(favoritoRepositoryPort.existePorPessoaEServico(pessoa, servico)).thenReturn(true);

        assertThrows(RecursoJaExisteException.class, () -> favoritoService.adicionar(requestMock()));
        verify(favoritoRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve adicionar favorito com sucesso")
    void adicionar_sucesso() {
        Pessoa pessoa = pessoaMock();
        Servico servico = servicoMock();

        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(servicoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(servico));
        when(favoritoRepositoryPort.existePorPessoaEServico(pessoa, servico)).thenReturn(false);

        assertDoesNotThrow(() -> favoritoService.adicionar(requestMock()));
        verify(favoritoRepositoryPort).salvar(any(Favorito.class));
    }

    // ─── remover ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao remover favorito inexistente")
    void remover_favoritoNaoEncontrado_deveLancarExcecao() {
        when(favoritoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> favoritoService.remover(99L));
        verify(favoritoRepositoryPort, never()).deletarPorId(any());
    }

    @Test
    @DisplayName("Deve remover favorito com sucesso")
    void remover_sucesso() {
        Favorito favorito = Favorito.builder().id(3L).build();
        when(favoritoRepositoryPort.buscarPorId(3L)).thenReturn(Optional.of(favorito));

        favoritoService.remover(3L);

        verify(favoritoRepositoryPort).deletarPorId(3L);
    }

    // ─── listar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar lista vazia ao listar favoritos quando pessoa não existir")
    void listar_pessoaNaoEncontrada_deveRetornarListaVazia() {
        when(pessoaRepositoryPort.existePorId(2L)).thenReturn(false);

        List<ServicoFavoritoResponse> resultado = favoritoService.listar(2L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista de favoritos com sucesso")
    void listar_sucesso() {
        Pessoa pessoa = pessoaMock();
        Servico servico = servicoMock();
        Favorito favorito =
                Favorito.builder().id(1L).pessoa(pessoa).servico(servico).build();

        when(pessoaRepositoryPort.existePorId(1L)).thenReturn(true);
        when(favoritoRepositoryPort.buscarPorPessoaId(1L)).thenReturn(List.of(favorito));

        List<ServicoFavoritoResponse> resultado = favoritoService.listar(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.getFirst().getIdFavorito());
        assertEquals(5L, resultado.getFirst().getIdServico());
        assertEquals("Lavagem", resultado.getFirst().getNome());
        assertEquals(BigDecimal.valueOf(80), resultado.getFirst().getPreco());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando pessoa não tiver favoritos")
    void listar_semFavoritos_deveRetornarListaVazia() {
        when(pessoaRepositoryPort.existePorId(1L)).thenReturn(true);
        when(favoritoRepositoryPort.buscarPorPessoaId(1L)).thenReturn(List.of());

        List<ServicoFavoritoResponse> resultado = favoritoService.listar(1L);

        assertTrue(resultado.isEmpty());
    }
}
