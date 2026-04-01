# Remediação OWASP A05 — Misconfiguration

## Resumo das Mudanças

Este documento descreve as mudanças implementadas para remediar vulnerabilidades OWASP A05 (Misconfiguration) relacionadas a **CORS permissivo** e **exposição do H2 Console**.

---

## 1. Problemas Identificados

### ❌ Antes

```java
// SecurityConfig.java — CORS permissivo em TODOS os profiles
config.setAllowedOrigins(List.of("*"));  // Aceita qualquer origem
config.setAllowedHeaders(List.of("*"));  // Aceita qualquer header
```

```properties
# application-prod.properties — H2 não estava explicitamente desabilitado
# (dependia de herança de perfil pai)

# URLs públicas incluíam /h2-console/** mesmo em produção
URLS_PUBLICAS = { ..., "/h2-console/**", ... }
```

### 🎯 Impacto

- **CORS:** Qualquer site malicioso poderia fazer requisições à API e ler respostas (credenciais, tokens)
- **H2 Console:** Exposição de banco de dados em-memória/local em produção potencialmente
- **Headers de Segurança:** Faltavam `X-Frame-Options: DENY`, `Strict-Transport-Security` em produção

---

## 2. Solução Implementada

### 2.1 Configuração Profile-Aware de CORS

#### Arquivo: `SecurityConfigProperties.java`

Nova classe com `@ConfigurationProperties` que centraliza:
- `security.cors.allowed-origins` — origens permitidas
- `security.cors.allow-credentials` — permite credenciais
- `security.cors.exposed-headers` — headers expostos na resposta
- `security.cors.allowed-methods` — métodos HTTP
- `security.cors.max-age` — cache de preflight

```java
@ConfigurationProperties(prefix = "security.cors")
public class SecurityConfigProperties {
    private List<String> allowedOrigins = new ArrayList<>();
    private boolean allowCredentials = false;
    // ...
}
```

#### Arquivo: `application-{profile}.properties`

**Dev:**
```properties
security.cors.allowed-origins=*
security.cors.allow-credentials=false
spring.h2.console.enabled=true
```

**Homolog:**
```properties
security.cors.allowed-origins=${ALLOWED_CORS_ORIGINS:}
security.cors.allow-credentials=true
spring.h2.console.enabled=false
server.forward-headers-strategy=native
```

**Prod:**
```properties
security.cors.allowed-origins=${ALLOWED_CORS_ORIGINS:}
security.cors.allow-credentials=true
spring.h2.console.enabled=false
server.forward-headers-strategy=native
```

### 2.2 H2 Console Condicional

#### Arquivo: `SecurityConfig.java`

```java
private String[] buildUrlsPublicas() {
    List<String> urls = new ArrayList<>();
    // ... add URLS_PUBLICAS_BASE ...
    
    // ✅ Incluir /h2-console/** APENAS se profile for 'dev'
    if (isDevProfile()) {
        urls.add("/h2-console/**");
    }
    return urls.toArray(new String[0]);
}

private boolean isDevProfile() {
    for (String profile : environment.getActiveProfiles()) {
        if ("dev".equals(profile)) return true;
    }
    return false;
}
```

### 2.3 Headers de Segurança Profile-Aware

```java
private void configureSecurityHeaders(HeadersConfigurer<?> headers) throws Exception {
    if (isDevProfile()) {
        // Dev: permitir frames (necessário para H2)
        headers.frameOptions(fo -> fo.sameOrigin());
    } else {
        // Prod/Homolog: negar frames
        headers.frameOptions(fo -> fo.deny())
               .xssProtection(xss -> xss.headerValue(
                   XXssProtectionHeaderWriter.HeaderValue.ONE_MODE_BLOCK));
        
        // Prod: HSTS (30+ dias)
        if (!isHomologProfile()) {
            headers.httpStrictTransportSecurity(
                hsts -> hsts.maxAgeInSeconds(31536000)
                           .includeSubDomains(true)
                           .preload(true));
        }
    }
}
```

### 2.4 Suporte a Proxy Reverso

#### Arquivo: `application-homolog.properties` e `application-prod.properties`

```properties
# Confiar em headers do proxy reverso
server.tomcat.remoteip.remote-ip-header=X-Forwarded-For
server.tomcat.remoteip.protocol-header=X-Forwarded-Proto
server.tomcat.remoteip.port-header=X-Forwarded-Port
server.forward-headers-strategy=native
```

Isso permite que:
- `X-Forwarded-For` seja lido como IP real do cliente
- `X-Forwarded-Proto` seja lido como protocolo original (http/https)
- CORS respeite a origem do proxy

---

## 3. Headers de Segurança Resultantes

### Dev
```http
X-Frame-Options: SAMEORIGIN       (permite H2)
X-Content-Type-Options: nosniff
```

### Homolog
```http
X-Frame-Options: DENY
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
```

### Prod
```http
X-Frame-Options: DENY
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
```

---

## 4. Variáveis de Ambiente Necessárias

### Homolog

```bash
export ALLOWED_CORS_ORIGINS="https://app-homolog.example.com,https://admin-homolog.example.com"
```

### Produção

```bash
export ALLOWED_CORS_ORIGINS="https://app.example.com,https://admin.example.com"
```

---

## 5. Testes

### Testes Unitários
- `SecurityConfigTest.java` — valida CORS por profile, H2 condicional

### Testes de Integração
- `SecurityIntegrationTest.java` — valida comportamento em runtime
  - ✅ H2 bloqueado em test
  - ✅ Swagger acessível
  - ✅ Endpoints protegidos requerem JWT

---

## 6. Fluxo de Requisição com Proxy

```
┌─────────────────┐
│   Cliente       │
└────────┬────────┘
         │ GET /api/ordem-servicos
         │ Origin: https://app.example.com
         │
         ├─ Proxy Nginx
         │  ├─ Valida CORS (permite apenas origins whitelist-adas)
         │  ├─ Reescreve headers
         │  │  ├─ X-Forwarded-For: 203.0.113.45
         │  │  ├─ X-Forwarded-Proto: https
         │  │  └─ X-Forwarded-Port: 443
         │  │
         └─▶ Spring API (localhost:8080)
            ├─ Lê X-Forwarded-* via server.forward-headers-strategy=native
            ├─ Confia no IP/protocolo do proxy
            ├─ Autentica via JWT
            └─ Retorna com headers de segurança
               ├─ X-Frame-Options: DENY
               ├─ Strict-Transport-Security: ...
               └─ [response payload]
```

---

## 7. Checklist de Implementação

- [x] **SecurityConfigProperties** — centralizar CORS config
- [x] **SecurityConfig** — profile-aware CORS, H2, headers
- [x] **application-dev.properties** — H2 habilitado, CORS permissivo
- [x] **application-homolog.properties** — H2 desabilitado, proxy settings, CORS via env
- [x] **application-prod.properties** — H2 desabilitado, proxy settings, CORS via env, HSTS
- [x] **Testes unitários** — SecurityConfigTest
- [x] **Testes de integração** — SecurityIntegrationTest
- [x] **Documentação** — este arquivo

---

## 8. Próximos Passos (Recomendações)

1. **DevOps/SRE** — Configurar no proxy Nginx/Apache:
   ```nginx
   add_header X-Content-Type-Options "nosniff";
   add_header X-Frame-Options "DENY";
   ```

2. **Validação** — Testar com ferramenta CORS (ex: curl):
   ```bash
   curl -H "Origin: https://evil.com" \
        -H "Access-Control-Request-Method: GET" \
        -X OPTIONS https://api.example.com/api/ordem-servicos
   ```

3. **Auditoria** — Revisar logs do proxy para requisições CORS bloqueadas

4. **Documentação** — Adicionar guia de deployment com ALLOWED_CORS_ORIGINS

---

## Referências

- [OWASP A05:2021 — Misconfiguration](https://owasp.org/Top10/A05_2021-Security_Misconfiguration/)
- [Spring Security CORS](https://spring.io/guides/gs/rest-cors/)
- [MDN CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [Nginx Proxy Headers](https://nginx.org/en/docs/http/ngx_http_proxy_module.html)

