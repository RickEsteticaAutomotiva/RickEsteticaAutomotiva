# Docker - Passo a Passo (Build + Run)

Guia enxuto para criar a imagem da API e subir com Docker Compose.

## 1) Pre-requisitos

- Docker Desktop instalado e rodando
- Variaveis de ambiente preenchidas em `.env.dev`
- RabbitMQ, Redis e MySQL disponiveis (locais ou remotos)

Se voce usa os containers locais do projeto (MySQL/RabbitMQ/Redis), suba antes:

```powershell
docker compose -f "C:\Users\Rodrigo\Documents\GitHub\RickEsteticaAutomotiva\api-agendamento-servicos\docker\docker-compose.yml" up -d
```

## 2) Build da imagem (dev)

Build simples:

```powershell
cd "C:\Users\Rodrigo\Documents\GitHub\RickEsteticaAutomotiva\api-agendamento-servicos"
docker build -t rick-api:dev .
```

Se o build falhar por falta do `librabbitmq:1.0-SNAPSHOT`, coloque o JAR em:
`docker\libs\librabbitmq-1.0-SNAPSHOT.jar` e rode:

```powershell
docker build -t rick-api:dev --build-arg LIBRABBITMQ_JAR=docker/libs/librabbitmq-1.0-SNAPSHOT.jar .
```

## 3) Subir a API (dev)

```powershell
docker compose --env-file .env.dev -f "C:\Users\Rodrigo\Documents\GitHub\RickEsteticaAutomotiva\api-agendamento-servicos\docker-compose.api.dev.yml" up -d
```

## 4) Teste rapido

```powershell
curl http://localhost:8080/api
```

## 5) Logs

```powershell
docker logs --tail 200 -f rick_api_dev
```

## 6) Parar a API

```powershell
docker compose --env-file .env.dev -f "C:\Users\Rodrigo\Documents\GitHub\RickEsteticaAutomotiva\api-agendamento-servicos\docker-compose.api.dev.yml" down
```

## (Opcional) Homolog e Prod

Build das imagens:

```powershell
docker build -t rick-api:homolog .
docker build -t rick-api:prod .
```

Subir homolog:

```powershell
docker compose --env-file .env.homolog -f "C:\Users\Rodrigo\Documents\GitHub\RickEsteticaAutomotiva\api-agendamento-servicos\docker-compose.api.homolog.yml" up -d
```

Subir prod:

```powershell
docker compose --env-file .env.prod -f "C:\Users\Rodrigo\Documents\GitHub\RickEsteticaAutomotiva\api-agendamento-servicos\docker-compose.api.prod.yml" up -d
```

