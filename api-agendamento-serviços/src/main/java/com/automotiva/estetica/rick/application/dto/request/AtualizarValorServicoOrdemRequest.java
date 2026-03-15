package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class AtualizarValorServicoOrdemRequest {

    @NotNull(message = "valorAplicado e obrigatorio")
    @DecimalMin(value = "0.00", message = "valorAplicado deve ser maior ou igual a zero")
    private BigDecimal valorAplicado;
}

