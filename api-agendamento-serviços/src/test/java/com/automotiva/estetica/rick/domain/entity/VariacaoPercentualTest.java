package com.automotiva.estetica.rick.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VariacaoPercentualTest {

    @Test
    @DisplayName("Deve calcular variação positiva corretamente")
    void calcular_variacaoPositiva() {
        BigDecimal resultado = VariacaoPercentual.calcular(150, 100);

        assertEquals(0, new BigDecimal("50.00").compareTo(resultado));
    }

    @Test
    @DisplayName("Deve calcular variação negativa corretamente")
    void calcular_variacaoNegativa() {
        BigDecimal resultado = VariacaoPercentual.calcular(50, 100);

        assertEquals(0, new BigDecimal("-50.00").compareTo(resultado));
    }

    @Test
    @DisplayName("Deve retornar zero quando o valor anterior for zero")
    void calcular_anteriorZero_deveRetornarZero() {
        BigDecimal resultado = VariacaoPercentual.calcular(BigDecimal.valueOf(1000), BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    @DisplayName("Deve retornar zero quando o valor anterior for nulo")
    void calcular_anteriorNulo_deveRetornarZero() {
        BigDecimal resultado = VariacaoPercentual.calcular(BigDecimal.valueOf(500), null);

        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    @DisplayName("Deve retornar zero quando ambos os valores forem zero")
    void calcular_ambosZero_deveRetornarZero() {
        BigDecimal resultado = VariacaoPercentual.calcular(BigDecimal.ZERO, BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    @DisplayName("Deve retornar zero quando atual for zero e anterior positivo")
    void calcular_atualZero_anteriorPositivo() {
        BigDecimal resultado = VariacaoPercentual.calcular(BigDecimal.ZERO, BigDecimal.valueOf(200));

        assertEquals(0, new BigDecimal("-100.00").compareTo(resultado));
    }

    @Test
    @DisplayName("Deve aceitar BigDecimal diretamente e calcular com 2 casas decimais")
    void calcular_comBigDecimal_precisaoDuasCasas() {
        BigDecimal resultado = VariacaoPercentual.calcular(BigDecimal.valueOf(110), BigDecimal.valueOf(100));

        assertEquals(0, new BigDecimal("10.00").compareTo(resultado));
    }
}
