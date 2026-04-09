package com.automotiva.estetica.rick.infrastructure.security;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriedades de segurança carregadas do arquivo
 * application-{profile}.properties.
 *
 * <p>
 * Permite configuração profile-aware de CORS origins e headers de segurança.
 *
 * <p>
 * Exemplo em application-prod.properties: ```
 * security.cors.allowed-origins=https://app.example.com,https://admin.example.com
 * security.cors.allow-credentials=true
 * security.cors.exposed-headers=Authorization,X-Total-Count ```
 */
@Data
@Component
@ConfigurationProperties(prefix = "security.cors")
public class SecurityConfigProperties {

    /**
     * Lista de origens CORS permitidas. Separadas por vírgula. Padrão em dev: "*"
     * Em produção, especificar domínios explicitamente.
     *
     * <p>
     * Exemplos: - dev: "*" - prod:
     * "https://app.example.com,https://admin.example.com"
     */
    private List<String> allowedOrigins = new ArrayList<>();

    /**
     * Métodos HTTP permitidos. Padrão: GET, POST, PUT, PATCH, DELETE, OPTIONS.
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

    /**
     * Headers permitidos na requisição. Padrão: "*"
     */
    private List<String> allowedHeaders = List.of("*");

    /**
     * Headers expostos na resposta. Exemplos: Authorization, X-Total-Count,
     * Content-Type.
     */
    private List<String> exposedHeaders = List.of("Authorization", "X-Total-Count", "Content-Type");

    /**
     * Se deve permitir credenciais (cookies, auth headers). Padrão em dev: false.
     * Em prod com proxy, true se necessário compartilhar credenciais.
     */
    private boolean allowCredentials = false;

    /**
     * Max age do preflight cache em segundos. Padrão: 3600 (1 hora).
     */
    private Long maxAge = 3600L;
}
