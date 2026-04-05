package com.automotiva.estetica.rick.infrastructure.security;

import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;

/**
 * Utilitário para redação de dados sensíveis em respostas de logs de erro.
 *
 * <p>
 * Quando administradores consultam logs de erro via API, os dados sensíveis
 * (payloads, stack traces, IPs de clientes) devem ser redados para reduzir
 * superfície de ataque. Conforme OWASP A09, logs consultáveis devem ser
 * protegidos contra acesso não autorizado.
 *
 * <p>
 * Camada: infrastructure/security.
 */
public final class ErroLogResponseRedactor {

    private ErroLogResponseRedactor() {
    }

    /**
     * Redaciona uma resposta de log de erro removendo dados sensíveis.
     *
     * <p>
     * Aplica mascaramento em:
     * <ul>
     * <li>payloadRequisicao — pode conter credenciais do usuário</li>
     * <li>queryParams — pode conter tokens ou IDs sensíveis</li>
     * <li>stackTrace — pode conter IPs, caminhos ou valores internos</li>
     * <li>usuarioEmail — PII que não deve ser exibido</li>
     * <li>ipCliente — pode permitir rastreamento de usuários</li>
     * </ul>
     *
     * @param response
     *            resposta de log que será redada
     * @return resposta com campos sensíveis mascarados
     */
    public static ErroLogResponse redactResponse(ErroLogResponse response) {
        if (response == null) {
            return null;
        }

        return ErroLogResponse.builder().id(response.getId()).timestamp(response.getTimestamp())
                .tipoExcecao(response.getTipoExcecao()).mensagem(response.getMensagem())
                // Stack trace já foi redated na persistência, mas reaplica por segurança
                .stackTrace(SensitiveDataRedactor.redactStackTrace(response.getStackTrace()))
                .endpoint(response.getEndpoint()).metodoHttp(response.getMetodoHttp())
                // Payload sempre redated
                .payloadRequisicao(SensitiveDataRedactor.redactPayload(response.getPayloadRequisicao()))
                // Query params sempre redated
                .queryParams(SensitiveDataRedactor.redactPayload(response.getQueryParams()))
                // Headers já foram redated na persistência, mas reaplica
                .headersRequisicao(response.getHeadersRequisicao())
                // Mascarar email: user@domain.com → u***@domain.com (mostra domínio apenas)
                .usuarioEmail(maskEmail(response.getUsuarioEmail()))
                // IP mascarado: 192.168.1.100 → 192.168.*.***
                .ipCliente(maskIpAddress(response.getIpCliente())).userAgent(response.getUserAgent())
                .statusHttp(response.getStatusHttp()).ambiente(response.getAmbiente()).build();
    }

    /**
     * Mascara e-mail deixando apenas domínio visível.
     *
     * <p>
     * Exemplo: user@example.com → u***@example.com
     */
    private static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email; // Email inválido ou muito curto
        }

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        // Mostrar apenas primeiro caractere + *** + domínio
        return localPart.substring(0, 1) + "***" + domain;
    }

    /**
     * Mascara endereço IPv4 para preservar apenas primeira octet.
     *
     * <p>
     * Exemplo: 192.168.1.100 → 192.168.*.***
     */
    private static String maskIpAddress(String ip) {
        if (ip == null || ip.isBlank()) {
            return ip;
        }

        // Validar formato básico de IPv4
        if (!ip.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
            return "***REDACTED_IP***"; // Não é IPv4 válido
        }

        String[] octets = ip.split("\\.");
        if (octets.length != 4) {
            return "***REDACTED_IP***";
        }

        // Mostrar apenas primeira octet: 192.168.*.***
        return octets[0] + "." + octets[1] + ".***.***";
    }
}
