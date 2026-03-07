package com.automotiva.estetica.rick.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FavoritoTest {

    @Test
    @DisplayName("criar deve retornar Favorito com pessoa e servico corretos")
    void criar_sucesso() {
        Pessoa pessoa = Pessoa.builder().id(1L).nome("Maria").build();
        Servico servico = Servico.builder().id(5L).nome("Lavagem").build();

        Favorito favorito = Favorito.criar(pessoa, servico);

        assertNotNull(favorito);
        assertEquals(pessoa, favorito.getPessoa());
        assertEquals(servico, favorito.getServico());
        assertNull(favorito.getId());
    }

    @Test
    @DisplayName("criar deve lançar IllegalArgumentException quando pessoa for nula")
    void criar_pessoaNula_deveLancarExcecao() {
        Servico servico = Servico.builder().id(5L).build();

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> Favorito.criar(null, servico));

        assertEquals("Pessoa não pode ser nula no favorito", ex.getMessage());
    }

    @Test
    @DisplayName("criar deve lançar IllegalArgumentException quando servico for nulo")
    void criar_servicoNulo_deveLancarExcecao() {
        Pessoa pessoa = Pessoa.builder().id(1L).build();

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> Favorito.criar(pessoa, null));

        assertEquals("Serviço não pode ser nulo no favorito", ex.getMessage());
    }
}
