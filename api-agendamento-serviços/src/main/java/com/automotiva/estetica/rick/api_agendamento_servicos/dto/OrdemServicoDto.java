package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoDto {
    private Long id;
    private Date dtConclusao;
    private String observacoes;
    private String status;
    private Long idAgendamento;
}
