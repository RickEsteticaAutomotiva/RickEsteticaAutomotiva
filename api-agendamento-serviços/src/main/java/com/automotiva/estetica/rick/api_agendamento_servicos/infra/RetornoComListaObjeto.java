package com.automotiva.estetica.rick.api_agendamento_servicos.infra;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetornoComListaObjeto<T> {
    private Integer statusCode;
    private String mensagem;
    private List<T> objeto;
}
