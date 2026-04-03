package com.automotiva.estetica.rick.infrastructure.security;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utilitário para redação de dados sensíveis em logs.
 *
 * <p>
 * Mascara valores de chaves sensíveis em JSON e query strings, preservando
 * estrutura para debugging sem expor PII/credenciais.
 *
 * <p>
 * Exemplo:
 *
 * <pre>
 * Input:  {"email":"user@test.com", "senha":"abc123", "nome":"João"}
 * Output: {"email":"user@test.com", "senha":"***REDACTED***", "nome":"João"}
 * </pre>
 */
public final class SensitiveDataRedactor {

    private static final String REDACTED = "***REDACTED***";

    /**
     * Chaves reconhecidas como sensíveis. Busca case-insensitive.
     */
    private static final Set<String> SENSITIVE_KEYS = new HashSet<>();

    static {
        SENSITIVE_KEYS.add("senha");
        SENSITIVE_KEYS.add("password");
        SENSITIVE_KEYS.add("token");
        SENSITIVE_KEYS.add("authorization");
        SENSITIVE_KEYS.add("cpf");
        SENSITIVE_KEYS.add("ssn");
        SENSITIVE_KEYS.add("credit_card");
        SENSITIVE_KEYS.add("creditcard");
        SENSITIVE_KEYS.add("api_key");
        SENSITIVE_KEYS.add("apikey");
        SENSITIVE_KEYS.add("secret");
        SENSITIVE_KEYS.add("refresh_token");
        SENSITIVE_KEYS.add("access_token");
        SENSITIVE_KEYS.add("pin");
        SENSITIVE_KEYS.add("otp");
        SENSITIVE_KEYS.add("jwt");
        SENSITIVE_KEYS.add("confirmar_senha");
        SENSITIVE_KEYS.add("refreshtoken");
    }

    private SensitiveDataRedactor() {
    }

    /**
     * Mascara valores sensíveis em payload JSON-like (ex: request body).
     *
     * @param payload
     *            string contendo JSON ou form-data
     * @return payload com valores sensíveis redados
     */
    public static String redactPayload(String payload) {
        if (payload == null || payload.isBlank()) {
            return payload;
        }

        String result = payload;

        // JSON: "chave": "valor" → "chave": "***REDACTED***"
        for (String key : SENSITIVE_KEYS) {
            result = redactJsonField(result, key);
            // Também tenta variações com underscore/hífen
            result = redactJsonField(result, key.replace("_", "-"));
            // Também tenta camelCase (remove underscores)
            result = redactJsonField(result, toCamelCase(key));
        }

        // Form data: chave=valor → chave=***REDACTED***
        for (String key : SENSITIVE_KEYS) {
            result = redactFormField(result, key);
            result = redactFormField(result, key.replace("_", "-"));
            result = redactFormField(result, toCamelCase(key));
        }

        // Query string: ?chave=valor → ?chave=***REDACTED***
        for (String key : SENSITIVE_KEYS) {
            result = redactQueryParam(result, key);
            result = redactQueryParam(result, key.replace("_", "-"));
            result = redactQueryParam(result, toCamelCase(key));
        }

        return result;
    }

    /**
     * Converte snake_case para camelCase.
     *
     * <p>
     * Ex: refresh_token → refreshToken
     */
    private static String toCamelCase(String input) {
        if (!input.contains("_")) {
            return input;
        }

        String[] parts = input.split("_");
        StringBuilder camelCase = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            camelCase.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
        }
        return camelCase.toString();
    }

    /**
     * Mascara valor de chave em campo JSON.
     *
     * <p>
     * Regex: "chave":\s*"[^"]*" → "chave": "***REDACTED***"
     */
    private static String redactJsonField(String input, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*\"[^\"]*\"",
                Pattern.CASE_INSENSITIVE);
        return pattern.matcher(input).replaceAll("\"" + fieldName + "\": \"" + REDACTED + "\"");
    }

    /**
     * Mascara valor de chave em form-data/urlencoded.
     *
     * <p>
     * Regex: chave=[^&]* → chave=***REDACTED***
     */
    private static String redactFormField(String input, String fieldName) {
        Pattern pattern = Pattern.compile(Pattern.quote(fieldName) + "=[^&]*", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(input).replaceAll(fieldName + "=" + REDACTED);
    }

    /**
     * Mascara valor de parâmetro em query string.
     *
     * <p>
     * Regex: ?chave=valor ou &chave=valor → ?chave=***REDACTED***
     */
    private static String redactQueryParam(String input, String paramName) {
        Pattern pattern = Pattern.compile("([?&])" + Pattern.quote(paramName) + "=[^&]*", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(input).replaceAll("$1" + paramName + "=" + REDACTED);
    }

    /**
     * Mascara valor de chave em headers (ex: Authorization).
     *
     * <p>
     * Regex: chave: valor → chave: ***REDACTED***
     */
    public static String redactHeader(String headerName, String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return headerValue;
        }

        if (SENSITIVE_KEYS.contains(headerName.toLowerCase())) {
            return REDACTED;
        }

        return headerValue;
    }
}
