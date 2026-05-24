# syntax=docker/dockerfile:1.4
# =====================================================
# Dockerfile — Multi-stage Build Otimizado para Produção
# =====================================================
# Build: docker build -t rick-api:latest .
# Run:   docker run -e SPRING_PROFILES_ACTIVE=prod rick-api:latest
# =====================================================

# =====================================================
# Stage 1: Build — Compile e Package com Maven
# =====================================================
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /build

ARG GH_USERNAME
ARG GH_TOKEN

# settings.xml com credenciais (não vai pra imagem final, só no builder)
RUN mkdir -p /root/.m2 && printf '%s\n' \
    '<settings>' \
    '  <servers>' \
    '    <server>' \
    '      <id>github</id>' \
    '      <username>'"${GH_USERNAME}"'</username>' \
    '      <password>'"${GH_TOKEN}"'</password>' \
    '    </server>' \
    '  </servers>' \
    '</settings>' > /root/.m2/settings.xml

# Copia apenas o necessário pra resolver dependências (camada cacheável)
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw

# Baixa dependências em cache mount (não vira parte da camada da imagem)
RUN --mount=type=cache,target=/root/.m2/repository \
    ./mvnw dependency:go-offline -B -ntp

# Compila com cache mount também
COPY src src
RUN --mount=type=cache,target=/root/.m2/repository \
    ./mvnw clean package -DskipTests -B -ntp && \
    cp target/api-agendamento-servicos-*.jar /build/app.jar

# =====================================================
# Stage 2: Runtime — Imagem final minimalista
# =====================================================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Cria usuário não-root e instala curl em uma única camada
RUN groupadd -r spring && useradd -r -g spring spring && \
    apt-get update && apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Copia jar e .env.example já com owner e permissões corretas (sem precisar de chmod/chown depois)
COPY --from=builder --chown=spring:spring --chmod=444 /build/app.jar app.jar
COPY --chown=spring:spring --chmod=444 .env.example .env.example

USER spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/swagger-ui.html || exit 1

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-XX:ActiveProcessorCount=2", "-jar", "app.jar"]

LABEL org.opencontainers.image.title="Rick Estética Automotiva API"
LABEL org.opencontainers.image.description="API de Agendamento de Serviços — OWASP A02 Compliant"
LABEL org.opencontainers.image.version="1.0.0"
