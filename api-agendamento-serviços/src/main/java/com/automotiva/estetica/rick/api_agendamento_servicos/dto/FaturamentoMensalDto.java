package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaturamentoMensalDto {
    private BigDecimal total;
    private BigDecimal lucro;
}
