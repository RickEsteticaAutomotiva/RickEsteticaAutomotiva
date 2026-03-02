package com.automotiva.estetica.rick.application.dto.response;

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
public class VeiculoResponse {

    private Long id;
    private Long idPessoa;
    private String placa;
    private String modelo;
    private String marca;
    private String porte;
    private String cor;
    private String ano;
}
