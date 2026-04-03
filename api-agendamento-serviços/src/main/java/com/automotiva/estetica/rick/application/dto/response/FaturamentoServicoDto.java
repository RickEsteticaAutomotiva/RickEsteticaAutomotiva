package com.automotiva.estetica.rick.application.dto.response;

import java.math.BigDecimal;

/**
 * DTO de saída da porta OrdemServicoRepositoryPort.buscarFaturamentoServicos.
 */
public record FaturamentoServicoDto(Long servicoId, String servico, Long categoriaId, String categoria,
        Long quantidadeVendida, BigDecimal faturamento) {
}
