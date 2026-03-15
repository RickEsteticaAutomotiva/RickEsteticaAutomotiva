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
public class FluxoCaixaResponse {

    private BigDecimal total;
    private BigDecimal lucro;
    private BigDecimal custo;
    private BigDecimal percentualLucro;
    private BigDecimal percentualCusto;
}

