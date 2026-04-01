# ✅ Checklist de Validação Final — OWASP A05

## 📋 Pré-Requisitos

- [ ] Java 21+ instalado (`java -version`)
- [ ] Maven 3.9+ instalado (`mvn -v`)
- [ ] Git configurado (`git config --list`)
- [ ] Acesso ao repositório
- [ ] Permissões para mergear para `main`

---

## 🔍 Validação Local (Dev)

### 1. Compilação

```bash
cd api-agendamento-serviços
mvn clean compile -DskipTests
```

**Status:** [ ] ✅ BUILD SUCCESS ou [ ] ❌ COMPILATION ERROR

**Se falhar:**
```bash
mvn clean compile -X 2>&1 | grep -A 5 ERROR
```

### 2. Testes Unitários

```bash
mvn test -Dtest=SecurityConfigTest -X
```

**Esperado:**
- [ ] ✅ CORS em dev permite "*"
- [ ] ✅ CORS em prod restringe
- [ ] ✅ H2 acessível em dev
- [ ] ✅ H2 bloqueado em prod

### 3. Testes de Integração

```bash
mvn test -Dtest=SecurityIntegrationTest -X
```

**Esperado:**
- [ ] ✅ H2 Console retorna 401 (bloqueado)
- [ ] ✅ Swagger acessível (200)
- [ ] ✅ Endpoints protegidos requerem JWT

### 4. Build Completo

```bash
mvn clean package -DskipTests
```

**Status:** [ ] ✅ JAR criado em `target/api-agendamento-servicos-*.jar`

---

## 🏗️ Validação de Código

### 1. SecurityConfigProperties.java

**Checklist:**
- [ ] ✅ `@ConfigurationProperties(prefix = "security.cors")`
- [ ] ✅ Campos: `allowedOrigins`, `allowCredentials`, `exposedHeaders`, `maxAge`
- [ ] ✅ Docs completos (Javadoc)
- [ ] ✅ Sem erros de compilação

### 2. SecurityConfig.java

**Checklist:**
- [ ] ✅ Injeta `SecurityConfigProperties`
- [ ] ✅ Injeta `Environment`
- [ ] ✅ Método `buildUrlsPublicas()` condiciona H2 por profile
- [ ] ✅ Método `isDevProfile()` implementado
- [ ] ✅ Método `isHomologProfile()` implementado
- [ ] ✅ Método `configureSecurityHeaders()` é profile-aware
- [ ] ✅ `corsConfigurationSource()` usa `SecurityConfigProperties`
- [ ] ✅ Sem erros de compilação

### 3. Application Properties

**Dev (`application-dev.properties`):**
- [ ] ✅ `spring.h2.console.enabled=true`
- [ ] ✅ `security.cors.allowed-origins=*`
- [ ] ✅ `security.cors.allow-credentials=false`

**Homolog (`application-homolog.properties`):**
- [ ] ✅ `spring.h2.console.enabled=false`
- [ ] ✅ `security.cors.allowed-origins=${ALLOWED_CORS_ORIGINS:}`
- [ ] ✅ `security.cors.allow-credentials=true`
- [ ] ✅ `server.forward-headers-strategy=native`
- [ ] ✅ `server.tomcat.remoteip.*` headers configurados

**Prod (`application-prod.properties`):**
- [ ] ✅ `spring.h2.console.enabled=false`
- [ ] ✅ `security.cors.allowed-origins=${ALLOWED_CORS_ORIGINS:}`
- [ ] ✅ `security.cors.allow-credentials=true`
- [ ] ✅ `server.forward-headers-strategy=native`
- [ ] ✅ `server.error.include-stacktrace=never`
- [ ] ✅ `server.error.include-message=never`

---

## 🧪 Testes de Requisições

### 1. Teste em Dev

```bash
# Iniciar aplicação
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev,swagger"

# Em outro terminal:

# H2 Console deve estar acessível
curl -v http://localhost:8080/api/h2-console/
# Status: 200 OK (com HTML do H2)
[ ] ✅ Esperado: 200 OK

# CORS deve permitir qualquer origem
curl -H "Origin: https://malicious.com" \
     -H "Access-Control-Request-Method: GET" \
     -X OPTIONS http://localhost:8080/api/ordem-servicos
# Header: Access-Control-Allow-Origin: * (em dev é aceitável)
[ ] ✅ Contém: Access-Control-Allow-Origin: *

# Swagger deve estar acessível
curl -v http://localhost:8080/api/swagger-ui.html | head -20
# Status: 200 OK
[ ] ✅ Esperado: 200 OK
```

### 2. Teste em Homolog/Prod (Simulado)

```bash
# Parar dev, iniciar com profile de teste
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=integration-test"

# H2 Console deve estar bloqueado
curl -v http://localhost:8080/api/h2-console/
# Status: 401 Unauthorized
[ ] ✅ Esperado: 401 Unauthorized

# Endpoint protegido sem token deve retornar 401
curl -v http://localhost:8080/api/ordem-servicos
# Status: 401 Unauthorized
[ ] ✅ Esperado: 401 Unauthorized

# Swagger ainda deve estar acessível (URL pública)
curl -v http://localhost:8080/api/swagger-ui.html | head -20
# Status: 200 OK
[ ] ✅ Esperado: 200 OK
```

---

## 📊 Verificação de Headers

### Em Desenvolvimento

```bash
curl -I http://localhost:8080/api/swagger-ui.html | grep -i "frame-options\|content-type\|hsts"
```

**Esperado:**
```
X-Frame-Options: SAMEORIGIN  ← Dev permite frames (H2 precisa)
X-Content-Type-Options: nosniff
(Sem HSTS em dev)
```

[ ] ✅ Headers corretos para dev

### Em Produção (Simulado)

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

curl -I http://localhost:8080/api/swagger-ui.html | grep -i "frame-options\|content-type\|hsts"
```

**Esperado:**
```
X-Frame-Options: DENY
X-Content-Type-Options: nosniff
Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
```

[ ] ✅ Headers corretos para produção

---

## 🔐 Testes de Segurança

### 1. CORS Blocking

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=integration-test"

# Em outro terminal:
curl -H "Origin: https://evil.com" \
     -H "Access-Control-Request-Method: POST" \
     -X OPTIONS http://localhost:8080/api/ordem-servicos -v

# Deve NÃO incluir Access-Control-Allow-Origin para evil.com
grep "Access-Control-Allow-Origin" # Não deve retornar nada ou retornar lista restrita
```

[ ] ✅ CORS bloqueando origem não-permitida

### 2. H2 Security

```bash
# H2 não deve estar em URLS_PUBLICAS quando profile != dev
curl -v http://localhost:8080/api/h2-console/ 2>&1 | grep "401\|403\|404"
```

[ ] ✅ H2 bloqueado com 401/404 fora de dev

### 3. JWT Required

```bash
curl -v http://localhost:8080/api/ordem-servicos 2>&1 | grep "401"
```

[ ] ✅ Endpoints protegidos requerem JWT

---

## 📚 Documentação

### 1. Arquivos Criados

- [ ] ✅ `SecurityConfigProperties.java` com Javadoc
- [ ] ✅ `SecurityConfig.java` com comentários explicativos
- [ ] ✅ `SECURITY_OWASP_A05_REMEDIATION.md` — detalhes técnicos
- [ ] ✅ `DEPLOYMENT_GUIDE.md` — guia de deployment
- [ ] ✅ `OWASP_A05_SUMMARY.md` — resumo executivo
- [ ] ✅ Testes com `@DisplayName` e comentários

### 2. README Atualizado (Opcional)

- [ ] ✅ README.md menciona remediação OWASP A05
- [ ] ✅ Seção "Segurança" adicionada com instrções

---

## 🚀 Antes do Merge

### Code Review

- [ ] ✅ Revisor 1: Aprova SecurityConfig.java
- [ ] ✅ Revisor 2: Aprova properties files
- [ ] ✅ Revisor 3: Aprova testes

### CI/CD Pipeline

- [ ] ✅ SonarQube: Sem issues bloqueantes
- [ ] ✅ CodeCov: Cobertura >80%
- [ ] ✅ Build pipeline: ✅ PASSED
- [ ] ✅ Test pipeline: ✅ ALL TESTS PASSED

### Checklist Final

- [ ] ✅ Branch está atualizado com `main`
- [ ] ✅ Commits estão bem organizados
- [ ] ✅ Commit messages em português claro
- [ ] ✅ Sem merge conflicts
- [ ] ✅ Sem files não-rastreados

---

## 🌍 Deployment

### Homologação

```bash
# 1. Set env var
export ALLOWED_CORS_ORIGINS="https://app-homolog.example.com,https://admin-homolog.example.com"

# 2. Deploy
java -jar api-agendamento-servicos-*.jar \
  --spring.profiles.active=homolog \
  --server.port=8080

# 3. Validar
curl -H "Origin: https://app-homolog.example.com" \
     -H "Access-Control-Request-Method: GET" \
     -X OPTIONS https://api-homolog.example.com/api/ordem-servicos

# 4. Verificar logs
tail -f application.log | grep -i "cors\|h2\|security"
```

**Status Homolog:**
- [ ] ✅ Deploy bem-sucedido
- [ ] ✅ H2 bloqueado (401/404)
- [ ] ✅ CORS funciona com origens permitidas
- [ ] ✅ Headers de segurança presentes
- [ ] ✅ Sem errors em logs

### Produção

```bash
# 1. Set env vars
export DB_URL="jdbc:mysql://prod-db:3306/rick_prod?..."
export DB_USERNAME="admin_prod"
export DB_PASSWORD="<super_secret>"
export ALLOWED_CORS_ORIGINS="https://app.example.com,https://admin.example.com"

# 2. Deploy
java -jar api-agendamento-servicos-*.jar \
  --spring.profiles.active=prod \
  --server.port=8080

# 3. Validar
curl -I https://api.example.com/api/swagger-ui.html | grep -i "hsts\|frame-options"

# 4. Monitorar por 24h
tail -f application.log | grep -i "401\|403\|cors"
```

**Status Produção:**
- [ ] ✅ Deploy bem-sucedido
- [ ] ✅ H2 bloqueado (401/404)
- [ ] ✅ HSTS header presente
- [ ] ✅ CORS whitelist funcionando
- [ ] ✅ Taxa de erro CORS aceitável (<0.1%)
- [ ] ✅ Sem dados sensíveis em stack traces

---

## 📞 Rollback (Se Necessário)

```bash
# Voltar para versão anterior
git revert <commit-hash>
# ou
git reset --hard HEAD~1

# Redeploy versão anterior
java -jar api-agendamento-servicos-*.jar
```

[ ] ✅ Plano de rollback testado (opcional)

---

## 🎯 Sign-Off

| Role | Nome | Data | Assinatura |
|------|------|------|-----------|
| Developer | ________________ | ________ | ________ |
| Reviewer | ________________ | ________ | ________ |
| QA Lead | ________________ | ________ | ________ |
| Security | ________________ | ________ | ________ |
| DevOps | ________________ | ________ | ________ |

---

**Notas:**

---

**Status Final:** 
- [ ] ✅ PRONTO PARA PRODUÇÃO
- [ ] ⚠️ AGUARDANDO CORREÇÕES
- [ ] ❌ BLOQUEADO

