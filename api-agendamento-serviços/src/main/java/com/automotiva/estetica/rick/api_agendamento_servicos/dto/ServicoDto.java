package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoDto {
    private Long id;
    private BigDecimal preco;
    private String descricao;
    private String nome;
}
