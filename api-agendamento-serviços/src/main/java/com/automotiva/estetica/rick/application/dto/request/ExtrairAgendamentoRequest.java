package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class ExtrairAgendamentoRequest {

    @NotBlank(message = "a imagem é obrigatória")
    private String imagemBase64;

    @NotBlank(message = "o tipo da imagem é obrigatório")
    private String mimeType;
}
