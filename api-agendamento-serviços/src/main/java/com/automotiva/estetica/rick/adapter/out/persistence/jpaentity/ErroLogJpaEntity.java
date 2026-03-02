package com.automotiva.estetica.rick.adapter.out.persistence.jpaentity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entidade JPA que persiste o log de erros em runtime.
 *
 * <p>Camada: adapter/out/persistence/jpa — infraestrutura de persistência.
 */
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "erro_log")
public class ErroLogJpaEntity extends BaseJpaEntity<Long> {

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "tipo_excecao", length = 512)
    private String tipoExcecao;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(length = 1024)
    private String endpoint;

    @Column(name = "metodo_http", length = 10)
    private String metodoHttp;

    @Column(name = "payload_requisicao", columnDefinition = "TEXT")
    private String payloadRequisicao;

    @Column(name = "query_params", columnDefinition = "TEXT")
    private String queryParams;

    @Column(name = "headers_requisicao", columnDefinition = "TEXT")
    private String headersRequisicao;

    @Column(name = "usuario_email", length = 255)
    private String usuarioEmail;

    @Column(name = "status_http")
    private Integer statusHttp;

    @Column(length = 50)
    private String ambiente;

    @Column(name = "ip_cliente", length = 50)
    private String ipCliente;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
}
