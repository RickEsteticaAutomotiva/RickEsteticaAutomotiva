package com.automotiva.estetica.rick.application.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoResponse {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagem;
    private Long categoriaId;
    private String categoriaNome;
}
