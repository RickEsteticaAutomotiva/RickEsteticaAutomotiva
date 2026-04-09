package com.automotiva.estetica.rick.infrastructure.filter;

import java.util.UUID;
import org.slf4j.MDC;

/**
 * Gerenciador de correlação de requisições para rastreabilidade de logs.
 *
 * <p>
 * Injeta um `requestId` único em cada requisição, armazenado no MDC (Mapped
 * Diagnostic Context) do SLF4J para que apareça automaticamente em todos os
 * logs dessa requisição.
 *
 * <p>
 * Use em `logback.xml`:
 *
 * <pre>
 * &lt;pattern&gt;%d{ISO8601} [%X{requestId}] %-5p %c{1}: %m%n&lt;/pattern&gt;
 * </pre>
 */
public final class RequestIdHolder {

    private static final String REQUEST_ID_KEY = "requestId";

    private RequestIdHolder() {
    }

    /**
     * Gera e armazena um `requestId` único no MDC para esta thread.
     *
     * @return o ID gerado
     */
    public static String generateAndStoreRequestId() {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_KEY, requestId);
        return requestId;
    }

    /**
     * Retorna o `requestId` atual armazenado no MDC.
     *
     * @return requestId ou null se não definido
     */
    public static String getRequestId() {
        return MDC.get(REQUEST_ID_KEY);
    }

    /**
     * Limpa o MDC (chamar no finally ou filtro de cleanup).
     */
    public static void clear() {
        MDC.remove(REQUEST_ID_KEY);
    }
}
