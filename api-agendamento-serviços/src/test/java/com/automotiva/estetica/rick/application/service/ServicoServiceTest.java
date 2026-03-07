package com.automotiva.estetica.rick.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.ServicoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoResponse;
import com.automotiva.estetica.rick.application.port.out.ServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Categoria;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock private ServicoRepositoryPort servicoRepositoryPort;

    @InjectMocks private ServicoService servicoService;

    private Servico servicoMock(Long id) {
        return Servico.builder()
                .id(id)
                .nome("Polimento")
                .descricao("Polimento completo")
                .preco(BigDecimal.valueOf(300))
                .imagem("pol.jpg")
                .categoria(Categoria.builder().id(2L).nome("Polimento").build())
                .build();
    }

    private ServicoRequest requestMock() {
        ServicoRequest req = new ServicoRequest();
        req.setNome("Polimento");
        req.setDescricao("Polimento completo");
        req.setPreco(BigDecimal.valueOf(300));
        req.setImagem("pol.jpg");
        req.setCategoriaId(2L);
        return req;
    }

    // ─── buscarTodos ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar página de serviços com sucesso")
    void buscarTodos_sucesso() {
        Page<Servico> page = new PageImpl<>(List.of(servicoMock(1L)));
        when(servicoRepositoryPort.buscarTodos(isNull(), any(Pageable.class))).thenReturn(page);

        PageRequest req = new PageRequest();
        req.setPagina(0);
        req.setTamanho(10);

        Page<ServicoResponse> resultado = servicoService.buscarTodos(req);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Polimento", resultado.getContent().getFirst().getNome());
    }

    @Test
    @DisplayName("Deve retornar página filtrada quando filtro for informado")
    void buscarTodos_comFiltro_devePassarFiltroAoRepositorio() {
        Page<Servico> page = new PageImpl<>(List.of(servicoMock(1L)));
        when(servicoRepositoryPort.buscarTodos(eq("Poli"), any(Pageable.class))).thenReturn(page);

        PageRequest req = new PageRequest();
        req.setPagina(0);
        req.setTamanho(10);
        req.setFiltro("Poli");

        Page<ServicoResponse> resultado = servicoService.buscarTodos(req);

        assertEquals(1, resultado.getTotalElements());
    }

    // ─── buscarPorId ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao buscar serviço por ID inexistente")
    void buscarPorId_inexistente_deveLancarExcecao() {
        when(servicoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> servicoService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve retornar serviço por ID com sucesso")
    void buscarPorId_sucesso() {
        when(servicoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(servicoMock(1L)));

        ServicoResponse resultado = servicoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Polimento", resultado.getNome());
        assertEquals(2L, resultado.getCategoriaId());
        assertEquals("Polimento", resultado.getCategoriaNome());
    }

    // ─── criar ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar serviço com sucesso")
    void criar_sucesso() {
        Servico salvo = servicoMock(10L);
        when(servicoRepositoryPort.salvar(any(Servico.class))).thenReturn(salvo);

        ServicoResponse resultado = servicoService.criar(requestMock());

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Polimento", resultado.getNome());
        verify(servicoRepositoryPort).salvar(any(Servico.class));
    }

    // ─── atualizar ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao atualizar serviço inexistente")
    void atualizar_inexistente_deveLancarExcecao() {
        when(servicoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> servicoService.atualizar(99L, requestMock()));
        verify(servicoRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve atualizar serviço com sucesso")
    void atualizar_sucesso() {
        Servico servico = servicoMock(1L);
        when(servicoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(servico));
        when(servicoRepositoryPort.salvar(servico)).thenReturn(servico);

        ServicoRequest req = requestMock();
        req.setNome("Polimento Cristalização");
        req.setPreco(BigDecimal.valueOf(450));

        ServicoResponse resultado = servicoService.atualizar(1L, req);

        assertNotNull(resultado);
        assertEquals("Polimento Cristalização", servico.getNome());
        assertEquals(BigDecimal.valueOf(450), servico.getPreco());
        verify(servicoRepositoryPort).salvar(servico);
    }

    // ─── deletar ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao deletar serviço inexistente")
    void deletar_inexistente_deveLancarExcecao() {
        when(servicoRepositoryPort.existePorId(99L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> servicoService.deletar(99L));
        verify(servicoRepositoryPort, never()).deletarPorId(any());
    }

    @Test
    @DisplayName("Deve deletar serviço com sucesso")
    void deletar_sucesso() {
        when(servicoRepositoryPort.existePorId(1L)).thenReturn(true);

        assertDoesNotThrow(() -> servicoService.deletar(1L));
        verify(servicoRepositoryPort).deletarPorId(1L);
    }
}
