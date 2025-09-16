package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoDto {
    @Id
    private Long id;
    private String placa;
    private String modelo;
    private String marca;
    private String porte;
    private String cor;
    private String ano;
    private Long pessoa;
}
