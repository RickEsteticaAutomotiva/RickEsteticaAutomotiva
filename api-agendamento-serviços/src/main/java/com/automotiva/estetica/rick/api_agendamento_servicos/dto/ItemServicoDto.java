package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemServicoDto {
    private Long id;
    private Long idOrdemServico;
    private Long idServico;
    private BigDecimal preco;
}
