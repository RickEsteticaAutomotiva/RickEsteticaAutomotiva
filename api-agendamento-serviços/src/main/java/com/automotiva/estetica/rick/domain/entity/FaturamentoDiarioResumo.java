package com.automotiva.estetica.rick.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FaturamentoDiarioResumo(LocalDate dia, BigDecimal totalDia) {
}
