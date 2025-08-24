package com.automotiva.estetica.rick.api_agendamento_servicos.infra;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetornoComObjeto<T> {
    private Integer statusCode;
    private String mensagem;
    private T objeto;
}
