package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QtdOrdensServicoConcluidasMensalResponseDto {
    private final Integer totalOrdensConcluidas;
    private final BigDecimal variacaoPercentual;
}
