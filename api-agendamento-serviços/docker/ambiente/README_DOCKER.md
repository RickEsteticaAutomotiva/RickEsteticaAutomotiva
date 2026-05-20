# Docker - API Agendamento Servicos

Guia rapido para subir a API via Docker Compose por ambiente, usando servicos externos (MySQL/RabbitMQ/SMTP).

## Pre-requisitos

- Docker Desktop com Docker Compose
- Arquivo de ambiente preenchido (.env.dev, .env.homolog, .env.prod)

## Build da imagem (dev)

Use o Dockerfile multi-stage do projeto.

```bash
docker build -t rick-api:dev .
```

## Dependencia local: librabbitmq

Para builds Docker, o Maven precisa do `librabbitmq:1.0-SNAPSHOT`. Caso este JAR nao esteja em um repositorio remoto, coloque o arquivo local em `docker/libs/librabbitmq-1.0-SNAPSHOT.jar` e rode o build com:

```bash
docker build -t rick-api:dev --build-arg LIBRABBITMQ_JAR=docker/libs/librabbitmq-1.0-SNAPSHOT.jar .
```

## Subir por ambiente

### Dev

```bash
docker compose --env-file .env.dev -f docker-compose.api.dev.yml up -d
```

### Homolog

```bash
docker compose --env-file .env.homolog -f docker-compose.api.homolog.yml up -d
```

### Prod

```bash
docker compose --env-file .env.prod -f docker-compose.api.prod.yml up -d
```

## Validacao rapida

```bash
curl http://localhost:8080/api
```

## Logs

```bash
docker compose -f docker-compose.api.dev.yml logs -f api
```

## Observacoes

- Os arquivos de compose usam healthcheck em `http://localhost:8080/api`.
- Ajuste `ALLOWED_CORS_ORIGINS` conforme ambiente.
- `LOGGING_LEVEL` controla o nivel de log da API.
