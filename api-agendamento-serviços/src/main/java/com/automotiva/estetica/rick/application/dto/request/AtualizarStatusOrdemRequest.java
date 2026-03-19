package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class AtualizarStatusOrdemRequest {

    @NotNull(message = "status e obrigatorio")
    @Min(value = 1, message = "status invalido")
    @Max(value = 5, message = "status invalido")
    private Long status;
}
