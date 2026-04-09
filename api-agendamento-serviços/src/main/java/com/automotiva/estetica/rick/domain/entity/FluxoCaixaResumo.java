package com.automotiva.estetica.rick.domain.entity;

import java.math.BigDecimal;

public record FluxoCaixaResumo(BigDecimal total, BigDecimal lucro, BigDecimal custo, BigDecimal percentualLucro,
        BigDecimal percentualCusto) {
}
