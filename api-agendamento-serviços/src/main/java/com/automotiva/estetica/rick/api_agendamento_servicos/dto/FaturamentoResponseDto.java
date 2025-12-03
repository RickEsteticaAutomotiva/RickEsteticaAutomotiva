package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FaturamentoResponseDto {
    private final BigDecimal faturamentoAtual;
    private final BigDecimal variacaoPercentual;
}
