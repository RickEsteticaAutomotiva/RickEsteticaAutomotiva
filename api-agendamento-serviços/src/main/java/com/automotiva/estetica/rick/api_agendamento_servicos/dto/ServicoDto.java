package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoDto {
    private Long id;
    private BigDecimal preco;
    private String descricao;
    private String nome;
    private String imagem;
    private CategoriaDto categoria;
}
