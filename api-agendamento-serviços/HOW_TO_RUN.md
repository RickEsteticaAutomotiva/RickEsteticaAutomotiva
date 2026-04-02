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
- phpMyAdmin: http://localhost:8090 (rick / rick@dev2024)
- RabbitMQ: http://localhost:15672 (guest / guest)

---

## 3️⃣ Secrets (Configurar Variáveis)

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

# Testes
./mvnw test
```

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

Última atualização: 01 de abril de 2026

