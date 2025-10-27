package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoDto {
    private Long id;
    private LocalDateTime dataAgendamento;
    private List<Long> servicos;
    private BigDecimal precoMinimo;
    private Long veiculo;
    private Long status;
    private String observacoes;
    private LocalDateTime dtConclusao;
    private Long motivo;
}
