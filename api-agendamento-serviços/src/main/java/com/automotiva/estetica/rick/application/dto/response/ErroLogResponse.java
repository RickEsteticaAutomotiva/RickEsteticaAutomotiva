package com.automotiva.estetica.rick.application.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO de resposta para consulta do log de erros. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErroLogResponse {

    private Long id;
    private LocalDateTime timestamp;
    private String tipoExcecao;
    private String mensagem;
    private String stackTrace;
    private String endpoint;
    private String metodoHttp;
    private String payloadRequisicao;
    private String queryParams;
    private String headersRequisicao;
    private String usuarioEmail;
    private Integer statusHttp;
    private String ambiente;
    private String ipCliente;
    private String userAgent;
}
