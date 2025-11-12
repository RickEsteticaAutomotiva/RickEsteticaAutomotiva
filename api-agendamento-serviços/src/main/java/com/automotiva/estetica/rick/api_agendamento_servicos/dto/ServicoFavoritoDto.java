package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import java.math.BigDecimal;
import lombok.*;

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
