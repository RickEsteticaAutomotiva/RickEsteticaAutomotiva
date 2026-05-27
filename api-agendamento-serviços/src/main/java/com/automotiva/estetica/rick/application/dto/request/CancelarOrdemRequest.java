package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.NotNull;
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
public class CancelarOrdemRequest {

    @NotNull(message = "O motivo é obrigatório")
    private Long motivo;
}


