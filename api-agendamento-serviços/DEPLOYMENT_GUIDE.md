# Guia de Deployment — OWASP A05 Remediação

## 🚀 Arquivos Modificados

| Arquivo | Modificação |
|---------|-------------|
| `SecurityConfig.java` | Profile-aware CORS, H2 condicional, headers de segurança |
| `SecurityConfigProperties.java` | **[NOVO]** Centralizar configurações de CORS via `@ConfigurationProperties` |
| `application-dev.properties` | Habilitar H2, CORS permissivo |
| `application-homolog.properties` | Desabilitar H2, proxy settings, CORS via env var |
| `application-prod.properties` | Desabilitar H2, proxy settings, CORS via env var, HSTS |
| `SecurityConfigTest.java` | **[NOVO]** Testes unitários de CORS e H2 |
| `SecurityIntegrationTest.java` | **[NOVO]** Testes de integração |
| `SECURITY_OWASP_A05_REMEDIATION.md` | **[NOVO]** Documentação detalhada |

---

## 📋 Checklist de Deployment

### 1. Desenvolvimento Local (Dev)

```bash
# Nenhuma ação necessária
# H2 Console acessível em: http://localhost:8080/api/h2-console
# CORS permite todas as origens
```

### 2. Homologação

#### 2.1 Variáveis de Ambiente

```bash
# Adicione no CI/CD ou no servidor:
export ALLOWED_CORS_ORIGINS="https://app-homolog.example.com,https://admin-homolog.example.com"

# Opcional (já com defaults em application-homolog.properties):
export DB_HOMOLOG_USERNAME="user_homolog"
export DB_HOMOLOG_PASSWORD="senha_homolog"
```

#### 2.2 Proxy Reverso (Nginx)

```nginx
upstream spring_api {
    server localhost:8080;
}

server {
    listen 443 ssl http2;
    server_name api-homolog.example.com;

    ssl_certificate /etc/ssl/certs/homolog.crt;
    ssl_certificate_key /etc/ssl/private/homolog.key;

    # Headers de segurança adicionais (Spring já envia alguns)
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;

    location /api {
        # Proxy headers para o Spring confiar no proxy
        proxy_pass http://spring_api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Port $server_port;

        # CORS será gerenciado pelo proxy + Spring (redundante mas seguro)
        add_header 'Access-Control-Allow-Origin' 'https://app-homolog.example.com' always;
        add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, PATCH, DELETE, OPTIONS' always;
        add_header 'Access-Control-Allow-Headers' 'Authorization, Content-Type' always;
        add_header 'Access-Control-Allow-Credentials' 'true' always;
    }

    location / {
        # Redirecionar para /api
        rewrite ^(.*)$ /api$1 permanent;
    }
}

# Redirecionar HTTP para HTTPS
server {
    listen 80;
    server_name api-homolog.example.com;
    return 301 https://$server_name$request_uri;
}
```

#### 2.3 Validar Deploy

```bash
# H2 Console deve estar bloqueado (401 ou 404)
curl -v https://api-homolog.example.com/api/h2-console/

# Swagger deve estar acessível (200)
curl -v https://api-homolog.example.com/api/swagger-ui.html

# Endpoint protegido deve rejeitar sem token (401)
curl -v https://api-homolog.example.com/api/ordem-servicos

# CORS deve rejeitar origens não-permitidas
curl -H "Origin: https://evil.com" \
     -H "Access-Control-Request-Method: GET" \
     -X OPTIONS https://api-homolog.example.com/api/ordem-servicos \
     -v
# Esperar: sem Access-Control-Allow-Origin ou 403
```

### 3. Produção

#### 3.1 Variáveis de Ambiente (Obrigatórias)

```bash
export DB_URL="jdbc:mysql://db.prod.example.com:3306/rick_prod?useUnicode=true&..."
export DB_USERNAME="admin_prod"
export DB_PASSWORD="<super_secret_password>"
export ALLOWED_CORS_ORIGINS="https://app.example.com,https://admin.example.com"
```

#### 3.2 Proxy Reverso (Nginx) — Produção

```nginx
upstream spring_api {
    server localhost:8080;
    keepalive 32;
}

# Rate limiting
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;

server {
    listen 443 ssl http2;
    server_name api.example.com;

    ssl_certificate /etc/ssl/certs/prod.crt;
    ssl_certificate_key /etc/ssl/private/prod.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES128-GCM-SHA256';
    ssl_prefer_server_ciphers on;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # HSTS — redundante (Spring já envia) mas reforça
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;

    # Rate limiting
    limit_req zone=api_limit burst=20 nodelay;

    location /api {
        proxy_pass http://spring_api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Port $server_port;

        # Connection pooling
        proxy_http_version 1.1;
        proxy_set_header Connection "";

        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;

        # CORS
        add_header 'Access-Control-Allow-Origin' 'https://app.example.com' always;
        add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, PATCH, DELETE, OPTIONS' always;
        add_header 'Access-Control-Allow-Headers' 'Authorization, Content-Type' always;
        add_header 'Access-Control-Allow-Credentials' 'true' always;
    }
}

# Redirecionar HTTP para HTTPS
server {
    listen 80;
    server_name api.example.com;
    return 301 https://$server_name$request_uri;
}

# HSTS preload (opcional mas recomendado)
# Registrar em https://hstspreload.org/
```

#### 3.3 Validar Deploy

```bash
# Todos os testes de homolog + verificações adicionais
curl -I https://api.example.com/api/ordem-servicos
# Deve conter: Strict-Transport-Security

# Teste de preflight CORS (origin não-permitida)
curl -H "Origin: https://evil.com" \
     -H "Access-Control-Request-Method: POST" \
     -X OPTIONS https://api.example.com/api/ordem-servicos \
     -v
# Não deve incluir Access-Control-Allow-Origin para evil.com
```

---

## 🔍 Monitoramento

### Logs a Monitorar

1. **Spring Boot Logs**
   ```
   # Sucesso — CORS permitido
   [DEBUG] CORS preflight request to "/"
   
   # Falha — CORS bloqueado (esperado em prod com origin não-permitida)
   [WARN] Rejecting CORS request
   ```

2. **Proxy Logs (Nginx)**
   ```
   # Bloqueado por rate limit
   limit_req:
   
   # Headers reescrito
   X-Forwarded-For: 203.0.113.45
   X-Forwarded-Proto: https
   ```

### Alertas Recomendados

1. ⚠️ Múltiplas tentativas de acesso a `/h2-console/**`
2. ⚠️ Múltiplos erros 401 (Unauthorized) — possível ataque
3. ⚠️ Taxa de erro CORS alta — possível misconfiguration

---

## 🚨 Troubleshooting

### Problema: H2 Console Bloqueado em Dev

```bash
# ❌ Erro: 401 Unauthorized ao acessar http://localhost:8080/api/h2-console
# ✅ Solução: Verificar profile ativo
spring.profiles.active=dev,swagger
```

### Problema: CORS Rejeitando Origem Válida

```bash
# ❌ Erro: Access-Control-Allow-Origin não retorna a origem
# ✅ Solução: Verificar ALLOWED_CORS_ORIGINS em homolog/prod
export ALLOWED_CORS_ORIGINS="https://app.example.com"
# Reiniciar aplicação
```

### Problema: Certificado SSL/TLS

```bash
# ❌ Erro: HSTS preload não aceita domínio
# ✅ Solução: Certificado deve ser válido + HTTPS obrigatório
# Registrar em https://hstspreload.org/ após validação
```

---

## 📚 Referências

- **OWASP Top 10 2021** — A05:2021 — Misconfiguration
  https://owasp.org/Top10/A05_2021-Security_Misconfiguration/

- **Spring Security**
  https://spring.io/projects/spring-security

- **Spring Boot Properties**
  https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html

- **Nginx Documentation**
  https://nginx.org/en/docs/

- **HSTS Preload**
  https://hstspreload.org/

---

## 👤 Suporte

Para dúvidas ou problemas:
1. Verificar `SECURITY_OWASP_A05_REMEDIATION.md`
2. Revisar logs da aplicação e proxy
3. Executar testes: `mvn test`
4. Consultar seção de Troubleshooting acima

