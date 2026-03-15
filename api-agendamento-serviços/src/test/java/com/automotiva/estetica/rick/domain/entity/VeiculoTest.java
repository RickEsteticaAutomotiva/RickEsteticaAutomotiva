package com.automotiva.estetica.rick.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VeiculoTest {

    private Veiculo veiculoMock() {
        return Veiculo.builder().id(1L).placa("ABC-1234").modelo("Civic").marca("Honda").porte("Médio").cor("Preto")
                .ano("2022").build();
    }

    @Test
    @DisplayName("Deve atualizar apenas campos não nulos")
    void atualizar_apenasNaoNulos() {
        Veiculo veiculo = veiculoMock();

        veiculo.atualizar(null, "Corolla", null, null, "Branco", null);

        assertEquals("ABC-1234", veiculo.getPlaca());
        assertEquals("Corolla", veiculo.getModelo());
        assertEquals("Honda", veiculo.getMarca());
        assertEquals("Médio", veiculo.getPorte());
        assertEquals("Branco", veiculo.getCor());
        assertEquals("2022", veiculo.getAno());
    }

    @Test
    @DisplayName("Deve atualizar todos os campos quando todos forem fornecidos")
    void atualizar_todosCampos() {
        Veiculo veiculo = veiculoMock();

        veiculo.atualizar("XYZ-9999", "Gol", "Volkswagen", "Pequeno", "Vermelho", "2020");

        assertEquals("XYZ-9999", veiculo.getPlaca());
        assertEquals("Gol", veiculo.getModelo());
        assertEquals("Volkswagen", veiculo.getMarca());
        assertEquals("Pequeno", veiculo.getPorte());
        assertEquals("Vermelho", veiculo.getCor());
        assertEquals("2020", veiculo.getAno());
    }

    @Test
    @DisplayName("Não deve alterar nenhum campo quando todos os parâmetros forem nulos")
    void atualizar_todosNulos_naDeveAlterarNada() {
        Veiculo veiculo = veiculoMock();

        veiculo.atualizar(null, null, null, null, null, null);

        assertEquals("ABC-1234", veiculo.getPlaca());
        assertEquals("Civic", veiculo.getModelo());
        assertEquals("Honda", veiculo.getMarca());
        assertEquals("Médio", veiculo.getPorte());
        assertEquals("Preto", veiculo.getCor());
        assertEquals("2022", veiculo.getAno());
    }
}
