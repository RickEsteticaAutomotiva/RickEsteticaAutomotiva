package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import jakarta.persistence.Id;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoDto {
    private Long id;
    private String placa;
    private String modelo;
    private String marca;
    private String porte;
    private String cor;
    private String ano;
    private Long idPessoa;
}
