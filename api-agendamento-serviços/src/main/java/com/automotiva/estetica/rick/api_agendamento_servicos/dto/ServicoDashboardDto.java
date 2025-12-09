package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ServicoDashboardDto {
    private String servico;
    private BigDecimal faturamento;
//    private Double lucro;
//    private Double percentual;
}
