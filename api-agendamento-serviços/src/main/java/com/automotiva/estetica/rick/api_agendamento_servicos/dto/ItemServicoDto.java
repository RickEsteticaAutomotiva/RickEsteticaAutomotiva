package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemServicoDto {
    private Long id;
    private Long idOrdemServico;
    private Long idServico;
    private BigDecimal preco;
}
