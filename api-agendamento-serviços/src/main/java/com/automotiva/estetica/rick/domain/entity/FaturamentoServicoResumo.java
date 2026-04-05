package com.automotiva.estetica.rick.domain.entity;

import java.math.BigDecimal;

public record FaturamentoServicoResumo(Long servicoId, String servico, Long categoriaId, String categoria,
        Long quantidadeVendida, BigDecimal faturamento) {
}
