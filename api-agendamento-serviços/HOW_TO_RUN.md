# 🚀 Como Rodar o Projeto

**Setup do zero em 20 minutos**

---

## 1️⃣ Pré-requisitos

```bash
✅ Java 21
✅ Maven 3.9+
✅ Docker + Docker Compose
✅ Git
```

Verificar:
```bash
java -version
mvn -version
docker --version
```

---

## 2️⃣ Clonar e Docker

```bash
# Clonar
git clone https://github.com/RickEsteticaAutomotiva/api-agendamento-servicos.git
cd api-agendamento-servicos

# Subir MySQL + RabbitMQ
docker compose -f docker/docker-compose.yml up -d

# Esperar 10 segundos para MySQL inicializar
```

**Acessos:**
- MySQL: localhost:3306 (rick / rick@dev2024)
- RabbitMQ: http://localhost:15672 (admin / 123456)

---

## 3️⃣ Secrets (Configurar Variáveis)

> O script agora gera secrets de dev únicos por execução (sem defaults estáticos).

**Windows PowerShell:**
```powershell
.\setup-dev-secrets.ps1
```

**Linux/Mac:**
```bash
chmod +x setup-dev-secrets.sh
./setup-dev-secrets.sh
```

---

## 4️⃣ Compilar

```bash
./mvnw clean compile
```

---

## 5️⃣ Rodar

**Windows:**
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev,swagger"
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev,swagger"
```

---

## ✅ Validar

```bash
# Swagger UI
http://localhost:8080/api/swagger-ui.html

# Teste GET
curl http://localhost:8080/api/categorias
# Esperado: [] ou lista de categorias (JSON)

# Testes (inclui ArchitectureLayersTest para garantir fronteiras Clean Architecture)
./mvnw test

# Regressao completa (unit + integration)
./mvnw verify
```

> Nota: em JDK 22+ o profile Maven `skip-pmd-on-jdk22plus` ativa automaticamente `pmd.skip=true` para evitar falha conhecida do PMD nessa runtime. Em JDK 21, PMD continua ativo normalmente.

### Regressao critica (focada apos refatoracoes de Clean Architecture)

```powershell
.\mvnw.cmd "-Dtest=ErroLogControllerIT,DashboardControllerIT,PessoaControllerIT,OrdemServicoControllerIT,OrdemServicoGestaoControllerIT,FluxoCriticoSmokeIT,SecurityIntegrationTest" test
```

---

## 🧱 Convenção de Arquitetura (estado atual)

- Pacotes de produção: `application`, `domain`, `infrastructure`
- Não usar novamente pacotes legados: `adapter` e `port`
- Nomenclatura alvo: `*GatewayImpl`, `*Publisher`, `*Repository`
- Regras protegidas por `ArchitectureLayersTest` (executado em `./mvnw test`)

---

## 🆘 Erros Comuns

| Erro | Solução |
|------|---------|
| Port 8080 in use | `lsof -i :8080` + `kill -9 <PID>` |
| Connection refused to MySQL | `docker ps` (containers rodando?) |
| Secrets não definidos | Reexecute setup-dev-secrets |
| BUILD FAILURE | `./mvnw clean install -DskipTests` |

---

**🎉 Pronto! API rodando em http://localhost:8080/api**

---

Para parar: `Ctrl+C` no terminal

Para próximas execuções (mais rápido):
```bash
# Terminal 1: Docker (se não tiver rodando)
docker compose -f docker/docker-compose.yml up -d

# Terminal 2: Aplicação
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev,swagger"
```

Última atualização: 03 de abril de 2026

