# Como Ativar Profiles no Spring Boot

Este guia explica como configurar e ativar diferentes profiles (ambientes) na sua aplicação Spring Boot.

## 📁 Estrutura de Arquivos

```
src/main/resources/
├── application.properties          # Configurações padrão/comuns
├── application-dev.properties      # Desenvolvimento
├── application-test.properties     # Testes
├── application-homolog.properties  # Homologação
└── application-prod.properties     # Produção
```

## 🔧 Formas de Ativar Profiles

### 1. **Via application.properties (Padrão)**
No arquivo `application.properties`:
```properties
spring.profiles.active=dev
```

### 2. **Via Linha de Comando**
Ao executar o JAR:
```bash
java -jar api-agendamento-servicos.jar --spring.profiles.active=prod
```

### 3. **Via IDE (IntelliJ IDEA)**
1. Clique em **Edit Configurations** (ao lado do botão Run)
2. Em **Program arguments**, adicione:
   ```
   --spring.profiles.active=dev
   ```
3. Clique em **Apply** e **OK**


### 4. **Via Variável de Ambiente**
**Windows:**
```cmd
set SPRING_PROFILES_ACTIVE=prod
java -jar api-agendamento-servicos.jar
```

**Linux/Mac:**
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar api-agendamento-servicos.jar
```

### 6. **Via Docker**
```dockerfile
ENV SPRING_PROFILES_ACTIVE=prod
```

Ou ao executar o container:
```bash
docker run -e SPRING_PROFILES_ACTIVE=prod minha-api
```

### 7. **Via Maven**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🔄 Múltiplos Profiles

Você pode ativar vários profiles ao mesmo tempo:

```properties
spring.profiles.active=dev,swagger,debug
```

Ou via linha de comando:
```bash
java -jar app.jar --spring.profiles.active=dev,swagger
```

## ✅ Como Verificar o Profile Ativo

### 1. **Nos logs de inicialização:**
```
2025-08-21 15:29:50.233  INFO 27844 --- [main] c.e.ApiApplication : The following profiles are active: dev
```

### 2. **Via endpoint do Actuator** (se habilitado):
```bash
curl http://localhost:8080/actuator/env
```

## 📝 Exemplo de Logs

Quando a aplicação iniciar, você verá algo assim:
```
2025-08-21 15:29:50.233  INFO 27844 --- [main] c.e.ApiApplication : Starting ApiApplication using Java 21
2025-08-21 15:29:50.233  INFO 27844 --- [main] c.e.ApiApplication : The following profiles are active: dev
2025-08-21 15:29:51.586  INFO 27844 --- [main] o.s.b.w.e.tomcat.TomcatWebServer : Tomcat initialized with port 8080 (http)
```

A linha que mostra **"The following profiles are active: dev"** confirma qual profile está sendo usado.