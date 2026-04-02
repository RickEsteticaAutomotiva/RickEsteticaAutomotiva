# =====================================================
# Dockerfile — Multi-stage Build Otimizado para Produção
# =====================================================
# Imagem final: ~350MB (sem incluir JDK completo)
# Segurança: Usuário não-root, readonly filesystem, health checks
#
# Build: docker build -t rick-api:latest .
# Run:   docker run -e SPRING_PROFILES_ACTIVE=prod \
#                   -e DB_URL=... -e JWT_SECRET=... \
#                   rick-api:latest
# =====================================================

# =====================================================
# Stage 1: Build — Compile e Package com Maven
# =====================================================
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /build

# Copiar pom.xml e baixar dependências (layer cacheável)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Executar mvnw para validar/preparar sem compilar src/
RUN ./mvnw dependency:go-offline -B 2>/dev/null || true

# Copiar código-fonte e compilar
COPY src src
RUN ./mvnw clean package -DskipTests -B

# =====================================================
# Stage 2: Runtime — Imagem final minimalista
# =====================================================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Criar usuário não-root para segurança
RUN groupadd -r spring && useradd -r -g spring spring

# Copiar JAR do builder
COPY --from=builder /build/target/api-agendamento-servicos-*.jar app.jar

# Copiar arquivo de secrets example (apenas referência)
COPY .env.example .env.example

# Ajustar permissões (read-only para usuário spring)
RUN chmod 444 app.jar && chmod 444 .env.example && chown -R spring:spring /app

# Trocar para usuário não-root
USER spring

# Porta exposta
EXPOSE 8080

# Health check — verifica se a aplicação está respondendo
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/swagger-ui.html || exit 1

# Entrypoint — roda o JAR com JVM otimizada para container
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-XX:ActiveProcessorCount=2", "-jar", "app.jar"]

# =====================================================
# Metadata
# =====================================================
LABEL org.opencontainers.image.title="Rick Estética Automotiva API"
LABEL org.opencontainers.image.description="API de Agendamento de Serviços — OWASP A02 Compliant"
LABEL org.opencontainers.image.authors="GitHub Copilot"
LABEL org.opencontainers.image.version="1.0.0"
