package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FaturamentoPeriodoResponseDto {
    private final LocalDate data;
    private final BigDecimal faturamentoDiario;
}
