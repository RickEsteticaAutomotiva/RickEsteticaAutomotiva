package com.automotiva.estetica.rick.domain.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entidade de domínio que representa o registro de um erro capturado em runtime. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErroLog {

    private Long id;

    /** Momento exato em que a exceção foi capturada. */
    private LocalDateTime timestamp;

    /** Nome completo da classe da exceção (ex: java.lang.NullPointerException). */
    private String tipoExcecao;

    /** Mensagem da exceção. */
    private String mensagem;

    /** Stack trace completo para reprodução do erro. */
    private String stackTrace;

    /** Endpoint HTTP que originou o erro (ex: /api/ordens/5). */
    private String endpoint;

    /** Método HTTP da requisição (GET, POST, PUT, PATCH, DELETE). */
    private String metodoHttp;

    /** Corpo da requisição (payload JSON) — útil para reprodução. */
    private String payloadRequisicao;

    /** Query parameters da URL (ex: page=0&size=10). */
    private String queryParams;

    /** Headers relevantes da requisição (sem Authorization para segurança). */
    private String headersRequisicao;

    /** E-mail ou username do usuário autenticado no momento do erro. */
    private String usuarioEmail;

    /** Status HTTP retornado ao cliente. */
    private Integer statusHttp;

    /** Profile Spring ativo no momento do erro (dev, homolog, prod). */
    private String ambiente;

    /** IP do cliente que fez a requisição. */
    private String ipCliente;

    /** User-Agent do cliente. */
    private String userAgent;
}
