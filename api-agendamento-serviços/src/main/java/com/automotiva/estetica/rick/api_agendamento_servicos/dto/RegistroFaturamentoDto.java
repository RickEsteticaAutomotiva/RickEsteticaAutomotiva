package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistroFaturamentoDto {
    private String categoriaNome;
    private String servicoNome;
    private BigDecimal totalPreco;
}



