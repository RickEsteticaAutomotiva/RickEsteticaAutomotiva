package com.automotiva.estetica.rick.domain.entity;

import java.math.BigDecimal;

public record FaturamentoServicoItemResumo(String servico, BigDecimal faturamento, Long quantidadeVendida) {
}
