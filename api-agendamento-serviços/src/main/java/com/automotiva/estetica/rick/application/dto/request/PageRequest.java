package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class PageRequest {

    private int pagina = 0;

    @Min(1)
    @Max(50)
    private int tamanho = 10;

    private String ordenarPor = "id";
    private String filtro;
}
