package com.automotiva.estetica.rick.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FaturamentoPeriodoResumo(LocalDate data, BigDecimal faturamentoDiario) {
}
