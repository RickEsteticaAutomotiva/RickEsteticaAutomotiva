# Relatório de Segurança - OWASP Top 10 (2021)

Projeto analisado: `api-agendamento-serviços`  
Data: 2026-03-17

## Escopo e método

- Análise estática de código e configurações do repositório.
- Referência principal: OWASP Top 10 (2021).
- Classificação por item:
  - `✅ Atendido`
  - `⚠️ Parcialmente atendido`
  - `❌ Não atendido`
- Observação: não foi executado pentest dinâmico/fuzzing. Achados são baseados nas evidências de código e configuração.

---

## 1) Broken Access Control

- **Status:** ❌ Não atendido
- **Explicação:** há múltiplos pontos com controle por role, mas sem validação de posse do recurso (BOLA/IDOR). Usuários autenticados com `ROLE_CLIENTE` conseguem consultar/alterar dados de outros usuários via IDs em path/body.
- **Evidências no projeto:**
  - `src/main/java/com/automotiva/estetica/rick/adapter/in/controller/PessoaController.java`
    - Endpoints `GET /pessoas/{id}`, `PUT /pessoas/{id}`, `DELETE /pessoas/{id}`, `PATCH /pessoas/{id}/senha` usam `@ClienteOnly`, porém aceitam `id` arbitrário.
  - `src/main/java/com/automotiva/estetica/rick/application/service/PessoaService.java`
    - `atualizar`, `deletar`, `atualizarSenha` operam diretamente no `id` recebido, sem comparar com usuário autenticado.
  - `src/main/java/com/automotiva/estetica/rick/adapter/in/controller/OrdemServicoController.java`
    - `GET /ordem-servicos/usuario/{id}` permite consulta de ordens de outro usuário.
  - `src/main/java/com/automotiva/estetica/rick/application/service/OrdemServicoService.java`
    - `buscarPorUsuarioId(Long usuarioId)` sem validação de ownership.
  - `src/main/java/com/automotiva/estetica/rick/adapter/in/controller/VeiculoController.java`
    - `GET /veiculos/pessoa/{id}` expõe veículos por `id` arbitrário.
  - `src/main/java/com/automotiva/estetica/rick/adapter/in/controller/CarrinhoController.java`
    - `GET /carrinhos/pessoa/{idPessoa}` e `DELETE /carrinhos/pessoa/{idPessoa}/limpar` sem ownership.
  - `src/main/java/com/automotiva/estetica/rick/adapter/in/controller/FavoritoController.java`
    - `GET /favoritos/pessoa/{idPessoa}` sem ownership.
  - Escalada de privilégio no cadastro:
    - `src/main/java/com/automotiva/estetica/rick/adapter/in/controller/PessoaController.java` (`POST /pessoas/` público)
    - `src/main/java/com/automotiva/estetica/rick/application/service/PessoaService.java` (`cadastrar` aceita `request.getRoles()` diretamente).
- **Impacto:**
  - Leitura/alteração/exclusão de dados de terceiros.
  - Troca de senha de outra conta por manipulação de ID.
  - Escalada para `ROLE_ADMIN` no auto-cadastro público, levando a comprometimento total da API.
- **Recomendações:**
  - Não confiar em ID do cliente para autorização de recursos sensíveis.
  - Derivar identidade do JWT (`SecurityContext`) e validar posse no service.
  - Para cadastro público, ignorar `roles` do request e forçar `ROLE_CLIENTE`.
  - Criar validação central `AuthorizationService` para `canAccessPessoa(id)`, `canAccessVeiculo(id)`, etc.
  - Adicionar testes de segurança (integração) cobrindo tentativas de acesso cruzado entre usuários.

---

## 2) Cryptographic Failures

- **Status:** ❌ Não atendido
- **Explicação:** existem boas práticas (BCrypt para senha), porém há exposição de segredos em texto claro e material criptográfico no repositório.
- **Evidências no projeto:**
  - `src/main/resources/application.properties`
    - `spring.mail.password` em texto claro.
    - `jwt.secret` em arquivo versionado.
    - `spring.rabbitmq.username/password` em texto claro.
  - `docker/docker-compose.yml`
    - `MYSQL_ROOT_PASSWORD`, `MYSQL_PASSWORD`, `RABBITMQ_DEFAULT_PASS` hardcoded.
  - `src/main/resources/db/seed/R__seed_dev.sql`
    - Senhas de usuários em texto claro (`senha123`) para várias contas de seed.
  - Ponto positivo:
    - `src/main/java/com/automotiva/estetica/rick/infrastructure/config/PasswordEncoderConfig.java`
    - `src/main/java/com/automotiva/estetica/rick/application/service/PessoaService.java` usam BCrypt para persistência de senha.
- **Impacto:**
  - Vazamento de segredo de JWT, SMTP e RabbitMQ permite forja de token, abuso de e-mail e acesso indevido a mensageria.
  - Se ambiente usar credenciais iguais às de desenvolvimento, risco de comprometimento lateral.
- **Recomendações:**
  - Remover segredos do Git; usar variáveis de ambiente/secret manager (Vault, AWS Secrets Manager, etc.).
  - Rotacionar imediatamente todos os segredos já expostos.
  - Em `dev` seed, armazenar somente hash BCrypt.
  - Aplicar política de segredo por ambiente e detecção automática de secret scanning no CI.

---

## 3) Injection

- **Status:** ✅ Atendido
- **Explicação:** não foram encontrados padrões de concatenação SQL dinâmica em consultas principais; uso predominante de JPA repository, Specification e parâmetros tipados.
- **Evidências no projeto:**
  - `src/main/java/com/automotiva/estetica/rick/adapter/out/persistence/ordemservico/OrdemServicoJpaRepository.java`
    - consultas com `@Query` parametrizadas (`:inicio`, `:fim`, etc.).
  - `src/main/java/com/automotiva/estetica/rick/adapter/out/persistence/ordemservico/OrdemServicoSpecification.java`
    - filtros via Criteria API (`cb.like`, `cb.equal`).
  - Não foram encontrados usos de `JdbcTemplate`, `EntityManager#createNativeQuery` ou concatenação manual de SQL.
- **Impacto:**
  - Baixa probabilidade de SQL injection no estado atual.
- **Recomendações:**
  - Manter padrão atual de parametrização.
  - Incluir testes negativos de injeção em filtros (`filtro`, `sort`, query params).

---

## 4) Insecure Design

- **Status:** ❌ Não atendido
- **Explicação:** problemas estruturais de autorização e de modelagem de fluxo de cadastro indicam falha de desenho de segurança (não apenas bug pontual).
- **Evidências no projeto:**
  - Ausência de política de ownership nos casos de uso (services aceitam IDs externos sem vínculo ao principal autenticado).
  - Cadastro público com campo de role aceito do cliente:
    - `src/main/java/com/automotiva/estetica/rick/application/dto/request/PessoaCadastroRequest.java`
    - `src/main/java/com/automotiva/estetica/rick/application/service/PessoaService.java`
  - Design de controle focado em role (`@ClienteOnly`) sem regra ABAC/ownership por recurso.
- **Impacto:**
  - Exposição horizontal de dados e potencial comprometimento completo por escalada de privilégio.
- **Recomendações:**
  - Definir explicitamente matriz de autorização por recurso/ação/escopo.
  - Separar fluxos: `auto-cadastro cliente` e `cadastro administrativo` (somente admin).
  - Adotar políticas de autorização no application layer (não apenas no controller).

---

## 5) Security Misconfiguration

- **Status:** ❌ Não atendido
- **Explicação:** há configurações inseguras de CORS e gestão de segredos; algumas configs de produção estão boas, mas baseline geral ainda expõe risco elevado.
- **Evidências no projeto:**
  - `src/main/java/com/automotiva/estetica/rick/infrastructure/security/SecurityConfig.java`
    - `config.setAllowedOrigins(List.of("*"))` e `setAllowedHeaders(List.of("*"))`.
  - `src/main/resources/application.properties`
    - segredos hardcoded e profile default `dev,swagger`.
  - `src/main/resources/application-prod.properties`
    - ponto positivo: uso de variáveis para DB e proteção de erro (`server.error.include-stacktrace=never`).
- **Impacto:**
  - Aumento da superfície de ataque em ambientes mal promovidos/configurados.
  - Risco operacional de exposição de interfaces e abuso de credenciais.
- **Recomendações:**
  - Restringir CORS por lista explícita de domínios confiáveis.
  - Separar propriedades locais via `.env`/secret manager, sem commitar segredos.
  - Garantir fail-fast de startup quando segredo obrigatório não estiver definido.

---

## 6) Vulnerable and Outdated Components

- **Status:** ⚠️ Parcialmente atendido
- **Explicação:** stack principal está relativamente atualizada, mas faltam controles contínuos de governança de dependências e há uso de artefato snapshot interno.
- **Evidências no projeto:**
  - `pom.xml`
    - Spring Boot `3.5.4` (atual).
    - Dependência interna `com.automotiva.estetica.rick:librabbitmq:1.0-SNAPSHOT` (imutabilidade fraca para supply chain).
  - Não foram encontrados workflows de CI em `.github/workflows` para SCA automática.
  - Verificação pontual de CVEs (subconjunto de libs versionadas) sem achados conhecidos nesta análise.
- **Impacto:**
  - Risco de entrada de vulnerabilidades futuras sem detecção antecipada.
  - Snapshot pode mudar sem trilha forte de integridade/reprodutibilidade.
- **Recomendações:**
  - Substituir `SNAPSHOT` por versão imutável release.
  - Adicionar SCA no CI (OWASP Dependency-Check, Snyk, Dependabot, osv-scanner).
  - Definir política de atualização periódica e SLA para correção de CVEs.

---

## 7) Identification and Authentication Failures

- **Status:** ⚠️ Parcialmente atendido
- **Explicação:** JWT stateless e hash de senha estão presentes, mas faltam camadas de proteção contra abuso de autenticação e ciclo de vida de token mais robusto.
- **Evidências no projeto:**
  - `src/main/java/com/automotiva/estetica/rick/infrastructure/security/JwtAuthFilter.java`
    - validação de bearer token e expiração.
  - `src/main/java/com/automotiva/estetica/rick/infrastructure/security/JwtServiceImpl.java`
    - emissão e validação de JWT.
  - `src/main/java/com/automotiva/estetica/rick/application/service/PessoaService.java`
    - autenticação via `AuthenticationManager`.
  - Não há evidência de:
    - lockout/rate-limit por tentativa de login;
    - refresh token/revogação;
    - MFA para perfis privilegiados.
- **Impacto:**
  - Facilita brute force e uso prolongado de token comprometido até expiração.
- **Recomendações:**
  - Rate limiting por IP/conta no `/pessoas/login`.
  - Implementar refresh token com rotação e blacklist/revogação.
  - MFA para `ADMIN`/`GERENTE`.
  - Incluir claims de `iss`, `aud` e validação estrita de contexto do token.

---

## 8) Software and Data Integrity Failures

- **Status:** ⚠️ Parcialmente atendido
- **Explicação:** Flyway e build estruturado ajudam consistência, mas faltam controles de integridade de pipeline/dependências em CI e governança de artefatos.
- **Evidências no projeto:**
  - `pom.xml`
    - build com plugins (PMD, Spotless, Surefire/Failsafe).
  - Ausência de workflows em `.github/workflows` para validação automática de segurança/integridade.
  - Dependência snapshot interna (`librabbitmq`) aumenta risco de drift de artefato.
- **Impacto:**
  - Maior risco de supply-chain compromise e build não reprodutível.
- **Recomendações:**
  - CI obrigatório com assinatura/verificação de artefatos e SCA.
  - Bloquear snapshots em branches protegidas/release.
  - Gerar SBOM (CycloneDX) e auditar periodicamente.

---

## 9) Security Logging and Monitoring Failures

- **Status:** ⚠️ Parcialmente atendido
- **Explicação:** há um mecanismo robusto de captura assíncrona de erro com contexto, porém sem evidência de alertas ativos, correlação/SIEM e monitoramento de eventos de segurança (ex.: brute force).
- **Evidências no projeto:**
  - `src/main/java/com/automotiva/estetica/rick/infrastructure/handler/GlobalExceptionHandler.java`
    - persistência de erro com endpoint, método, IP, user-agent e payload.
  - `src/main/java/com/automotiva/estetica/rick/application/service/ErroLogService.java`
    - gravação assíncrona e purga programada.
  - `src/main/java/com/automotiva/estetica/rick/infrastructure/filter/RequestCachingFilter.java`
    - suporte a captura de payload de request.
  - Não há evidência de integração com SIEM/alerta em tempo real.
- **Impacto:**
  - Detecção tardia de ataque e resposta a incidente mais lenta.
- **Recomendações:**
  - Publicar eventos críticos (401/403 repetidos, falhas de login, alterações sensíveis) para observabilidade central.
  - Configurar alertas por limiar e playbooks de resposta.
  - Evitar logging de dados sensíveis em payload quando possível (mascaramento adicional).

---

## 10) Server-Side Request Forgery (SSRF)

- **Status:** ✅ Atendido
- **Explicação:** não foram identificados endpoints que aceitam URL arbitrária do usuário para chamadas outbound HTTP no backend atual.
- **Evidências no projeto:**
  - Não há uso de `RestTemplate`, `WebClient` ou `HttpClient` no código analisado.
  - Integrações presentes são SMTP (`EmailAdapter`) e AMQP (`RabbitOrdemServicoPublisher`), sem entrada de URL externa por usuário.
- **Impacto:**
  - Risco SSRF atualmente baixo no escopo analisado.
- **Recomendações:**
  - Manter proibido aceitar URL de destino vinda do cliente sem allowlist.
  - Se integração HTTP for adicionada, aplicar egress allowlist, timeout, bloqueio de ranges internos e DNS rebinding protection.

---

## Priorização de riscos (ordem de correção)

1. **Crítico:** Broken Access Control (BOLA/IDOR + auto-cadastro com role arbitrária).
2. **Crítico:** Cryptographic Failures (segredos em repositório e credenciais hardcoded).
3. **Alto:** Insecure Design (ausência de modelo de autorização por ownership).
4. **Médio:** Security Misconfiguration (CORS aberto e perfil/segredos inseguros por padrão).
5. **Médio:** Identification and Authentication Failures (sem rate limit/MFA/revogação).
6. **Médio:** Software/Data Integrity e componentes (governança de dependências e CI de segurança incompletos).
7. **Baixo atual:** SSRF e Injection (bons controles no estado atual, manter vigilância).

---

## Quick Wins sugeridos (primeiras 2 sprints)

- Remover `roles` do payload de cadastro público e forçar `ROLE_CLIENTE` no service.
- Implementar checagem de ownership em todos endpoints com `id` de recurso/usuário.
- Rotacionar e externalizar todos segredos de `application.properties` e `docker-compose.yml`.
- Fechar CORS para domínios confiáveis por ambiente.
- Adicionar rate-limit no login e trilha de auditoria para tentativas suspeitas.
- Incluir SCA automática no CI e bloquear `SNAPSHOT` em release.

---

## Nota final

Este relatório identifica vulnerabilidades reais com base em evidências de código/configuração do repositório. Para fechar diagnóstico de exploração prática e impacto operacional completo, recomenda-se complementar com testes dinâmicos (DAST), pentest autenticado e revisão de infraestrutura/deploy.
