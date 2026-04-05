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
        // Credenciais e autenticação
        SENSITIVE_KEYS.add("senha");
        SENSITIVE_KEYS.add("password");
        SENSITIVE_KEYS.add("token");
        SENSITIVE_KEYS.add("authorization");
        SENSITIVE_KEYS.add("confirmar_senha");
        SENSITIVE_KEYS.add("confirm_password");
        SENSITIVE_KEYS.add("refreshtoken");
        SENSITIVE_KEYS.add("refresh_token");
        SENSITIVE_KEYS.add("access_token");
        SENSITIVE_KEYS.add("jwt");
        SENSITIVE_KEYS.add("bearer");

        // Identificadores pessoais (PII)
        SENSITIVE_KEYS.add("cpf");
        SENSITIVE_KEYS.add("cnpj");
        SENSITIVE_KEYS.add("ssn");
        SENSITIVE_KEYS.add("rg");
        SENSITIVE_KEYS.add("data_nascimento");
        SENSITIVE_KEYS.add("dob");
        SENSITIVE_KEYS.add("telefone");
        SENSITIVE_KEYS.add("phone");
        SENSITIVE_KEYS.add("celular");
        SENSITIVE_KEYS.add("mobile");
        SENSITIVE_KEYS.add("data_nasc");
        SENSITIVE_KEYS.add("birthdate");

        // Dados financeiros
        SENSITIVE_KEYS.add("credit_card");
        SENSITIVE_KEYS.add("creditcard");
        SENSITIVE_KEYS.add("cartao");
        SENSITIVE_KEYS.add("card_number");
        SENSITIVE_KEYS.add("cardnumber");
        SENSITIVE_KEYS.add("cvv");
        SENSITIVE_KEYS.add("cvc");
        SENSITIVE_KEYS.add("banco");
        SENSITIVE_KEYS.add("conta");
        SENSITIVE_KEYS.add("account");
        SENSITIVE_KEYS.add("routing");
        SENSITIVE_KEYS.add("iban");
        SENSITIVE_KEYS.add("swift");
        SENSITIVE_KEYS.add("pix");

        // Chaves e secrets
        SENSITIVE_KEYS.add("api_key");
        SENSITIVE_KEYS.add("apikey");
        SENSITIVE_KEYS.add("secret");
        SENSITIVE_KEYS.add("private_key");
        SENSITIVE_KEYS.add("privatekey");
        SENSITIVE_KEYS.add("client_secret");
        SENSITIVE_KEYS.add("clientsecret");
        SENSITIVE_KEYS.add("access_code");
        SENSITIVE_KEYS.add("accesscode");

        // Códigos e OTP
        SENSITIVE_KEYS.add("pin");
        SENSITIVE_KEYS.add("otp");
        SENSITIVE_KEYS.add("code");
        SENSITIVE_KEYS.add("verification_code");
        SENSITIVE_KEYS.add("verificationcode");
        SENSITIVE_KEYS.add("2fa");
        SENSITIVE_KEYS.add("mfa");
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

    /**
     * Mascara dados sensíveis em stack trace.
     *
     * <p>
     * Remove padrões de IPv4, emails, paths e tokens que possam aparecer em stack
     * traces. Stack traces são frequentemente salvos em logs e podem vazar PII.
     *
     * @param stackTrace
     *            stack trace da exceção
     * @return stack trace com dados sensíveis removidos
     */
    public static String redactStackTrace(String stackTrace) {
        if (stackTrace == null || stackTrace.isBlank()) {
            return stackTrace;
        }

        String result = stackTrace;

        // Redactar valores sensíveis que podem aparecer na stack trace
        for (String key : SENSITIVE_KEYS) {
            result = redactJsonField(result, key);
            result = redactJsonField(result, key.replace("_", "-"));
            result = redactJsonField(result, toCamelCase(key));
        }

        // Redactar IPv4 addresses: xxx.xxx.xxx.xxx → ***REDACTED_IP***
        result = result.replaceAll("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b", "***REDACTED_IP***");

        // Redactar email addresses: user@domain.com → user@***REDACTED***.com
        result = result.replaceAll("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+)", "$1@***REDACTED***");

        // Redactar possíveis JWTs (longas strings base64 com . separadores)
        result = result.replaceAll("eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+", "***JWT_REDACTED***");

        // Redactar caminhos de arquivo que podem conter nomes de usuários
        // (/home/username/...)
        result = result.replaceAll("/home/[^/\\s]+", "/home/***REDACTED***");
        result = result.replaceAll("C:\\\\Users\\\\[^\\\\\\s]+", "C:\\\\Users\\\\***REDACTED***");

        return result;
    }
}
