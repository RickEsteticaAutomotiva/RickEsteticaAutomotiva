package com.automotiva.estetica.rick.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de saída da porta OrdemServicoRepositoryPort.buscarFaturamentoPorDia.
 * Substitui o List&lt;Object[]&gt; que vazava detalhe de implementação JPA pela porta de domínio.
 */
public record FaturamentoDiarioDto(LocalDate dia, BigDecimal totalDia) {}
