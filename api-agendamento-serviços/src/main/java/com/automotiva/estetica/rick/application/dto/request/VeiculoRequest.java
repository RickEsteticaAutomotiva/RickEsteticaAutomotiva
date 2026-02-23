package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class VeiculoRequest {

    @NotNull(message = "O ID da pessoa é obrigatório")
    private Long idPessoa;

    @NotBlank(message = "A placa é obrigatória")
    private String placa;

    private String modelo;
    private String marca;
    private String porte;
    private String cor;
    private String ano;
}
