package com.automotiva.estetica.rick.domain.entity;

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
public class ItemServico {

    private Long id;
    private Servico servico;
    private OrdemServico ordemServico;
    private BigDecimal preco;
}
