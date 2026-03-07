package com.automotiva.estetica.rick.domain.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object que encapsula o cálculo de variação percentual entre dois períodos. Regra de
 * domínio: se o valor anterior for zero ou nulo, a variação é zero.
 */
public final class VariacaoPercentual {

    private VariacaoPercentual() {}

    /**
     * Calcula a variação percentual entre dois valores numéricos.
     *
     * @param atual valor do período atual
     * @param anterior valor do período anterior
     * @return variação em %, com 2 casas decimais; zero quando anterior é zero ou nulo
     */
    public static BigDecimal calcular(Number atual, Number anterior) {
        BigDecimal a = toBigDecimal(atual);
        BigDecimal b = toBigDecimal(anterior);
        if (b == null || b.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return a.subtract(b)
                .divide(b, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal toBigDecimal(Number n) {
        if (n == null) return BigDecimal.ZERO;
        if (n instanceof BigDecimal bd) return bd;
        return BigDecimal.valueOf(n.doubleValue());
    }
}
