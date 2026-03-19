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
public class ProximoAgendamentoResponse {

    private Long ordemServicoId;
    private String servico;
    private String hora;
    private String data;
    private String clienteNome;
    private String veiculoDescricao;
    private Long status;
}

