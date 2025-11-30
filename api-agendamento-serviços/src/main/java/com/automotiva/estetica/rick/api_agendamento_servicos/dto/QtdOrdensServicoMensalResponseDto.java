package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QtdOrdensServicoMensalResponseDto {
    private final Integer totalOrdens;
    private final BigDecimal variacaoPercentual;
}
