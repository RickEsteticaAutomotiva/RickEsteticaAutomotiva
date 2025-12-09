package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FluxoCaixaDto {
    private BigDecimal total;
    private BigDecimal lucro;
    private BigDecimal custo;
    private BigDecimal percentualCusto;
    private BigDecimal percentualLucro;


}
