package com.automotiva.estetica.rick.api_agendamento_servicos.infra;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetornoSemObjeto {
    private Integer statusCose;
    private String mensagem;
}
