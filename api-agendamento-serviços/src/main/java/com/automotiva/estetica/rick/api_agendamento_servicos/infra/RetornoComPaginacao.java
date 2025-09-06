package com.automotiva.estetica.rick.api_agendamento_servicos.infra;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetornoComPaginacao<T> {
    private int statusCode;
    private String mensagem;
    private List<T> conteudo;
    private int paginaAtual;
    private int totalPaginas;
    private long totalElementos;
    private int tamanhoPagina;
    private boolean ultimaPagina;
    private boolean primeiraPagina;
}