package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PessoaPageRequest {
    private int pagina = 0;

    @Min(1)
    @Max(50)
    private int tamanho = 10;
    private String ordenarPor = "id";
    private String filtro;
}
