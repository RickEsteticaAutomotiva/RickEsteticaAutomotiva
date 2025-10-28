package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoFavoritoDto {
    private Long idFavorito;
    private Long idServico;
    private BigDecimal preco;
    private String descricao;
    private String nome;
}
