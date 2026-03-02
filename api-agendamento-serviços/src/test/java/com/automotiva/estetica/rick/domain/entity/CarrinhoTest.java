package com.automotiva.estetica.rick.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarrinhoTest {

    @Test
    @DisplayName("criar deve retornar Carrinho com pessoa e servico corretos")
    void criar_sucesso() {
        Pessoa pessoa = Pessoa.builder().id(1L).nome("João").build();
        Servico servico = Servico.builder().id(2L).nome("Polimento").build();

        Carrinho carrinho = Carrinho.criar(pessoa, servico);

        assertNotNull(carrinho);
        assertEquals(pessoa, carrinho.getPessoa());
        assertEquals(servico, carrinho.getServico());
        assertNull(carrinho.getId());
    }

    @Test
    @DisplayName("criar deve lançar IllegalArgumentException quando pessoa for nula")
    void criar_pessoaNula_deveLancarExcecao() {
        Servico servico = Servico.builder().id(2L).build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Carrinho.criar(null, servico));

        assertEquals("Pessoa não pode ser nula no carrinho", ex.getMessage());
    }

    @Test
    @DisplayName("criar deve lançar IllegalArgumentException quando servico for nulo")
    void criar_servicoNulo_deveLancarExcecao() {
        Pessoa pessoa = Pessoa.builder().id(1L).build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Carrinho.criar(pessoa, null));

        assertEquals("Serviço não pode ser nulo no carrinho", ex.getMessage());
    }
}
