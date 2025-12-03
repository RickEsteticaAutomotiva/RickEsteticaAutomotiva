package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoDto {
    @NotNull
    private Long idPessoa;

    @NotNull
    private Long idServico;
}
