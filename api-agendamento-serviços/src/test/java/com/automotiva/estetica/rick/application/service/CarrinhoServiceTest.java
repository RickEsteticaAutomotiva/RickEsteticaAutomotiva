package com.automotiva.estetica.rick.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.dto.request.CarrinhoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoCarrinhoResponse;
import com.automotiva.estetica.rick.application.port.out.CarrinhoRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.PessoaRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.ServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Carrinho;
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
class CarrinhoServiceTest {

    @Mock private CarrinhoRepositoryPort carrinhoRepositoryPort;

    @Mock private PessoaRepositoryPort pessoaRepositoryPort;

    @Mock private ServicoRepositoryPort servicoRepositoryPort;

    @InjectMocks private CarrinhoService carrinhoService;

    private Pessoa pessoaMock() {
        return Pessoa.builder().id(1L).nome("João").build();
    }

    private Servico servicoMock() {
        return Servico.builder()
                .id(10L)
                .nome("Polimento")
                .descricao("Polimento completo")
                .preco(BigDecimal.valueOf(250))
                .imagem("imagem.jpg")
                .build();
    }

    private CarrinhoRequest requestMock() {
        CarrinhoRequest req = new CarrinhoRequest();
        req.setIdPessoa(1L);
        req.setIdServico(10L);
        return req;
    }

    // ─── adicionar ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao adicionar quando pessoa não existir")
    void adicionar_pessoaNaoEncontrada_deveLancarExcecao() {
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> carrinhoService.adicionar(requestMock()));
        verify(servicoRepositoryPort, never()).buscarPorId(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar quando serviço não existir")
    void adicionar_servicoNaoEncontrado_deveLancarExcecao() {
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoaMock()));
        when(servicoRepositoryPort.buscarPorId(10L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> carrinhoService.adicionar(requestMock()));
        verify(carrinhoRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar quando serviço já estiver no carrinho")
    void adicionar_servicoJaNoCarrinho_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();
        Servico servico = servicoMock();

        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(servicoRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(servico));
        when(carrinhoRepositoryPort.existePorPessoaEServico(pessoa, servico)).thenReturn(true);

        assertThrows(
                RecursoJaExisteException.class, () -> carrinhoService.adicionar(requestMock()));
        verify(carrinhoRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve adicionar serviço ao carrinho com sucesso")
    void adicionar_sucesso() {
        Pessoa pessoa = pessoaMock();
        Servico servico = servicoMock();

        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(servicoRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(servico));
        when(carrinhoRepositoryPort.existePorPessoaEServico(pessoa, servico)).thenReturn(false);

        assertDoesNotThrow(() -> carrinhoService.adicionar(requestMock()));
        verify(carrinhoRepositoryPort).salvar(any(Carrinho.class));
    }

    // ─── remover ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao remover item de carrinho inexistente")
    void remover_itemNaoEncontrado_deveLancarExcecao() {
        when(carrinhoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> carrinhoService.remover(99L));
        verify(carrinhoRepositoryPort, never()).deletarPorId(any());
    }

    @Test
    @DisplayName("Deve remover item do carrinho com sucesso")
    void remover_sucesso() {
        Carrinho carrinho = Carrinho.builder().id(5L).build();
        when(carrinhoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(carrinho));

        carrinhoService.remover(5L);

        verify(carrinhoRepositoryPort).deletarPorId(5L);
    }

    // ─── limparCarrinhoPessoa ────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao limpar carrinho quando pessoa não existir")
    void limparCarrinhoPessoa_pessoaNaoEncontrada_deveLancarExcecao() {
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> carrinhoService.limparCarrinhoPessoa(1L));
        verify(carrinhoRepositoryPort, never()).deletarTodos(any());
    }

    @Test
    @DisplayName("Deve limpar carrinho da pessoa com sucesso quando houver itens")
    void limparCarrinhoPessoa_comItens_deveDeletar() {
        Pessoa pessoa = pessoaMock();
        Carrinho item = Carrinho.builder().id(1L).build();

        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(carrinhoRepositoryPort.buscarPorPessoaId(1L)).thenReturn(List.of(item));

        carrinhoService.limparCarrinhoPessoa(1L);

        verify(carrinhoRepositoryPort).deletarTodos(List.of(item));
    }

    @Test
    @DisplayName("Não deve chamar deletarTodos quando carrinho estiver vazio")
    void limparCarrinhoPessoa_carrinhoVazio_naoDeveDeletar() {
        Pessoa pessoa = pessoaMock();

        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(carrinhoRepositoryPort.buscarPorPessoaId(1L)).thenReturn(List.of());

        carrinhoService.limparCarrinhoPessoa(1L);

        verify(carrinhoRepositoryPort, never()).deletarTodos(any());
    }

    // ─── listar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar lista vazia ao listar quando pessoa não existir")
    void listar_pessoaNaoEncontrada_deveRetornarListaVazia() {
        when(pessoaRepositoryPort.existePorId(2L)).thenReturn(false);

        List<ServicoCarrinhoResponse> resultado = carrinhoService.listar(2L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista de serviços do carrinho com sucesso")
    void listar_sucesso() {
        Pessoa pessoa = pessoaMock();
        Servico servico = servicoMock();
        Carrinho carrinho = Carrinho.builder().id(1L).pessoa(pessoa).servico(servico).build();

        when(pessoaRepositoryPort.existePorId(1L)).thenReturn(true);
        when(carrinhoRepositoryPort.buscarPorPessoaId(1L)).thenReturn(List.of(carrinho));

        List<ServicoCarrinhoResponse> resultado = carrinhoService.listar(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.getFirst().getIdCarrinho());
        assertEquals(10L, resultado.getFirst().getIdServico());
        assertEquals("Polimento", resultado.getFirst().getNome());
        assertEquals(BigDecimal.valueOf(250), resultado.getFirst().getPreco());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando carrinho não tiver itens")
    void listar_carrinhoVazio_deveRetornarListaVazia() {
        when(pessoaRepositoryPort.existePorId(1L)).thenReturn(true);
        when(carrinhoRepositoryPort.buscarPorPessoaId(1L)).thenReturn(List.of());

        List<ServicoCarrinhoResponse> resultado = carrinhoService.listar(1L);

        assertTrue(resultado.isEmpty());
    }
}
