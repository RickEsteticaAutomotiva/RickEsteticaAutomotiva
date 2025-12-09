package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class CategoriaDashboardDto {
    private String categoria;
    private Double totalCategoria;
    private List<ServicoDashboardDto> servicos;
}
