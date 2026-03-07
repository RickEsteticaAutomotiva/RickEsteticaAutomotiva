package com.automotiva.estetica.rick.application.service;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.dto.request.VeiculoRequest;
import com.automotiva.estetica.rick.application.dto.response.VeiculoResponse;
import com.automotiva.estetica.rick.application.port.out.PessoaRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.VeiculoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
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
class VeiculoServiceTest {

    @Mock private VeiculoRepositoryPort veiculoRepositoryPort;

    @Mock private PessoaRepositoryPort pessoaRepositoryPort;

    @InjectMocks private VeiculoService veiculoService;

    private VeiculoRequest requestMock() {
        VeiculoRequest req = new VeiculoRequest();
        req.setIdPessoa(1L);
        req.setPlaca("ABC-1234");
        req.setModelo("Civic");
        req.setMarca("Honda");
        req.setPorte("Médio");
        req.setCor("Preto");
        req.setAno("2022");
        return req;
    }

    private Veiculo veiculoMock(Long id) {
        Pessoa pessoa = Pessoa.builder().id(1L).build();
        return Veiculo.builder()
                .id(id)
                .placa("ABC-1234")
                .modelo("Civic")
                .marca("Honda")
                .porte("Médio")
                .cor("Preto")
                .ano("2022")
                .pessoa(pessoa)
                .build();
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar veículo quando pessoa não existir")
    void cadastrar_pessoaNaoEncontrada_deveLancarExcecao() {
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class, () -> veiculoService.cadastrar(requestMock()));
    }

    @Test
    @DisplayName("Deve cadastrar veículo com sucesso")
    void cadastrar_sucesso() {
        Pessoa pessoa = Pessoa.builder().id(1L).build();
        Veiculo salvo = veiculoMock(10L);

        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(veiculoRepositoryPort.salvar(any(Veiculo.class))).thenReturn(salvo);

        VeiculoResponse result = veiculoService.cadastrar(requestMock());

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(veiculoRepositoryPort).salvar(any(Veiculo.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar todos quando nenhum veículo existir")
    void buscarTodos_listaVazia_deveRetornarListaVazia() {
        when(veiculoRepositoryPort.buscarTodos()).thenReturn(emptyList());

        List<VeiculoResponse> result = veiculoService.buscarTodos();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista de veículos quando existir ao menos um")
    void buscarTodos_sucesso() {
        when(veiculoRepositoryPort.buscarTodos()).thenReturn(List.of(veiculoMock(1L)));

        List<VeiculoResponse> result = veiculoService.buscarTodos();

        assertEquals(1, result.size());
        assertEquals("ABC-1234", result.getFirst().getPlaca());
    }

    @Test
    @DisplayName(
            "Deve retornar lista vazia quando pessoa não existir ao buscar veículos por pessoa")
    void buscarPorPessoaId_pessoaNaoEncontrada_deveRetornarListaVazia() {
        when(pessoaRepositoryPort.existePorId(10L)).thenReturn(false);

        List<VeiculoResponse> result = veiculoService.buscarPorPessoaId(10L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando pessoa existe mas não possui veículos")
    void buscarPorPessoaId_semVeiculos_deveRetornarListaVazia() {
        when(pessoaRepositoryPort.existePorId(5L)).thenReturn(true);
        when(veiculoRepositoryPort.buscarPorPessoaId(5L)).thenReturn(emptyList());

        List<VeiculoResponse> result = veiculoService.buscarPorPessoaId(5L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista de veículos da pessoa com sucesso")
    void buscarPorPessoaId_sucesso() {
        when(pessoaRepositoryPort.existePorId(3L)).thenReturn(true);
        when(veiculoRepositoryPort.buscarPorPessoaId(3L)).thenReturn(List.of(veiculoMock(1L)));

        List<VeiculoResponse> result = veiculoService.buscarPorPessoaId(3L);

        assertEquals(1, result.size());
        verify(veiculoRepositoryPort).buscarPorPessoaId(3L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar veículo inexistente")
    void atualizar_inexistente_deveLancarExcecao() {
        when(veiculoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> veiculoService.atualizar(99L, requestMock()));
    }

    @Test
    @DisplayName("Deve atualizar veículo com sucesso")
    void atualizar_sucesso() {
        Veiculo veiculo = veiculoMock(1L);
        when(veiculoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(veiculo));

        VeiculoRequest req = requestMock();
        req.setModelo("Corolla");

        veiculoService.atualizar(1L, req);

        verify(veiculoRepositoryPort).salvar(veiculo);
        assertEquals("Corolla", veiculo.getModelo());
    }
}
