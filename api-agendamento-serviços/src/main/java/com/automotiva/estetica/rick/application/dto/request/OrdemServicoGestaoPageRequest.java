package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
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
public class OrdemServicoGestaoPageRequest {

    private Long status;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    private int pagina = 0;

    @Min(1)
    @Max(50)
    private int tamanho = 20;

    private String ordenarPor = "dataAgendamento";
    private String direcao = "desc";
}

