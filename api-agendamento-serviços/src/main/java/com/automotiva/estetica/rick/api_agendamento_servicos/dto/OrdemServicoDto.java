package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoDto {
    private Long id;
    private LocalDateTime dtConclusao;
    private String observacoes;
    private LocalDateTime dataAgendamento;
    private Long status;
    private Long veiculo;
    private Long motivo;
}
