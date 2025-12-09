package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelamentoDto {
    private String tipo;
    private long quantidade;
}