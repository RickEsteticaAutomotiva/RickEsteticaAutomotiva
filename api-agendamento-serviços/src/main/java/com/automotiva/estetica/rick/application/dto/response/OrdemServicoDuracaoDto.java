package com.automotiva.estetica.rick.application.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrdemServicoDuracaoDto {
    private Long ordemServicoId;
    private LocalDateTime dataAgendamento;
    private int duracaoTotal;
}
