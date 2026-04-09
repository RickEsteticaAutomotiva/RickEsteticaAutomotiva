package com.automotiva.estetica.rick.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServicoTest {

    @Test
    @DisplayName("deve converter duracao em minutos")
    void getDuracaoMinutos_deveConverter() {
        Servico servico = Servico.builder().duracaoMinutos(150).build();

        int minutos = servico.getDuracaoMinutos();

        assertEquals(150, minutos);
    }
}
