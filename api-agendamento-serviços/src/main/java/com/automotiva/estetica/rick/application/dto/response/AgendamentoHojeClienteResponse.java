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
public class AgendamentoHojeClienteResponse {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
}
