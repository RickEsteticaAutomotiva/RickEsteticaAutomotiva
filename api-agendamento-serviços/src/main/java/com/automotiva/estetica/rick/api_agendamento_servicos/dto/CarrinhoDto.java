package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrinhoDto {
    private Long idPessoa;
    private Long idServico;
}
