package com.automotiva.estetica.rick.application.service;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.dto.request.CategoriaRequest;
import com.automotiva.estetica.rick.application.dto.response.CategoriaResponse;
import com.automotiva.estetica.rick.application.port.out.CategoriaRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepositoryPort categoriaRepositoryPort;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaMock(Long id, String nome) {
        return Categoria.builder().id(id).nome(nome).build();
    }

    // ─── buscarTodas ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar lista de categorias com sucesso")
    void buscarTodas_sucesso() {
        when(categoriaRepositoryPort.buscarTodas())
                .thenReturn(List.of(categoriaMock(1L, "Lavagem"), categoriaMock(2L, "Polimento")));

        List<CategoriaResponse> resultado = categoriaService.buscarTodas();

        assertEquals(2, resultado.size());
        assertEquals("Lavagem", resultado.get(0).getNome());
        assertEquals("Polimento", resultado.get(1).getNome());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver categorias")
    void buscarTodas_listaVazia_deveRetornarVazia() {
        when(categoriaRepositoryPort.buscarTodas()).thenReturn(emptyList());

        List<CategoriaResponse> resultado = categoriaService.buscarTodas();

        assertTrue(resultado.isEmpty());
    }

    // ─── criar ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void criar_sucesso() {
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Detalhamento");

        assertDoesNotThrow(() -> categoriaService.criar(request));
        verify(categoriaRepositoryPort).salvar(any(Categoria.class));
    }

    // ─── atualizar ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao atualizar categoria inexistente")
    void atualizar_inexistente_deveLancarExcecao() {
        when(categoriaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Novo Nome");

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> categoriaService.atualizar(99L, request));
        verify(categoriaRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void atualizar_sucesso() {
        Categoria categoria = categoriaMock(1L, "Lavagem");
        when(categoriaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepositoryPort.salvar(categoria)).thenReturn(categoria);

        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Lavagem Premium");

        CategoriaResponse resultado = categoriaService.atualizar(1L, request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(categoriaRepositoryPort).salvar(categoria);
    }

    @Test
    @DisplayName("Não deve alterar nome quando request tiver nome em branco")
    void atualizar_nomeEmBranco_naoDeveAlterarNome() {
        Categoria categoria = categoriaMock(1L, "Lavagem");
        when(categoriaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepositoryPort.salvar(categoria)).thenReturn(categoria);

        CategoriaRequest request = new CategoriaRequest();
        request.setNome("  ");

        categoriaService.atualizar(1L, request);

        assertEquals("Lavagem", categoria.getNome());
    }
}
