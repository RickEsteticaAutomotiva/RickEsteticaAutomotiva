package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServicoDashboardDto {
    private String servico;
    private Double faturamento;
//    private Double lucro;
//    private Double percentual;
}
