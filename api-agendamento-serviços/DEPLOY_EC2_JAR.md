# Deploy EC2 (JAR) - API Agendamento Servicos

Guia para subir a API Spring Boot via `java -jar` em uma instancia AWS EC2 (sem Docker).

## 1) Escopo e premissas

- Este guia usa o profile `prod`.
- O artefato esperado e um fat jar Spring Boot gerado pelo Maven.
- A API sobe em `8080` e context path `/api`.
- Banco MySQL e RabbitMQ devem estar acessiveis pela EC2 (na propria EC2 ou externos).

Baseado no projeto:
- `src/main/resources/application.properties`
- `src/main/resources/application-prod.properties`
- `.env.prod`
- `src/main/java/com/automotiva/estetica/rick/infrastructure/config/SecretsValidator.java`

## 2) Requisitos de infraestrutura

- EC2 Linux (Amazon Linux 2023 recomendado).
- Java 21 instalado na EC2.
- Security Group da EC2 com:
  - `22/tcp` liberado somente para seu IP.
  - `8080/tcp` liberado apenas para ALB/Nginx (evite 0.0.0.0/0 em producao).
- Banco MySQL e RabbitMQ com regras de rede permitindo trafego da EC2.

## 3) Build do JAR

Execute no repositorio local (ou em pipeline CI):

```bash
./mvnw clean package -DskipTests
```

Artefato esperado:

```bash
target/api-agendamento-servicos-0.0.1-SNAPSHOT.jar
```

## 4) Preparar EC2 (Amazon Linux 2023)

Conecte na instancia:

```bash
ssh -i /caminho/chave.pem ec2-user@SEU_IP_OU_DNS
```

Instale Java 21 e utilitarios:

```bash
sudo dnf update -y
sudo dnf install -y java-21-amazon-corretto-headless
java -version
```

Crie estrutura de diretorios:

```bash
sudo mkdir -p /opt/rick-api
sudo mkdir -p /etc/rick-api
sudo useradd --system --no-create-home --shell /sbin/nologin rickapi
sudo chown -R rickapi:rickapi /opt/rick-api
sudo chmod 750 /opt/rick-api
```

## 5) Enviar JAR para EC2

No seu computador local:

```bash
scp -i /caminho/chave.pem target/api-agendamento-servicos-0.0.1-SNAPSHOT.jar ec2-user@SEU_IP_OU_DNS:/tmp/app.jar
```

Na EC2:

```bash
sudo mv /tmp/app.jar /opt/rick-api/app.jar
sudo chown rickapi:rickapi /opt/rick-api/app.jar
sudo chmod 540 /opt/rick-api/app.jar
```

## 6) Configurar variaveis de ambiente (producao)

Crie o arquivo de ambiente:

```bash
sudo tee /etc/rick-api/rick-api.env > /dev/null << 'EOF'
SPRING_PROFILES_ACTIVE=prod

DB_URL=jdbc:mysql://SEU_DB_HOST:3306/rick_prod?useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Sao_Paulo&useSSL=true
DB_USERNAME=rick_prod_user
DB_PASSWORD=SENHA_FORTE_DB

JWT_SECRET=SEU_SECRET_BASE64_FORTE

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=seu-email@seudominio.com
MAIL_PASSWORD=SENHA_APP_EMAIL

RABBITMQ_HOST=SEU_RABBIT_HOST
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=prod_user
RABBITMQ_PASSWORD=SENHA_FORTE_RABBIT

ALLOWED_CORS_ORIGINS=https://app.seudominio.com,https://admin.seudominio.com
EOF
```

Permissoes do arquivo de segredo:

```bash
sudo chown root:rickapi /etc/rick-api/rick-api.env
sudo chmod 640 /etc/rick-api/rick-api.env
```

Observacoes importantes:
- Em `prod`, o projeto exige secrets validos (classe `SecretsValidator`).
- Se `JWT_SECRET`, `DB_PASSWORD`, `MAIL_PASSWORD` ou `RABBITMQ_PASSWORD` estiverem invalidos, a aplicacao aborta no startup.

## 7) Criar servico systemd

Crie o unit file:

```bash
sudo tee /etc/systemd/system/rick-api.service > /dev/null << 'EOF'
[Unit]
Description=Rick API (Spring Boot JAR)
After=network.target
Wants=network.target

[Service]
Type=simple
User=rickapi
Group=rickapi
EnvironmentFile=/etc/rick-api/rick-api.env
WorkingDirectory=/opt/rick-api
ExecStart=/usr/bin/java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:ActiveProcessorCount=2 -jar /opt/rick-api/app.jar
SuccessExitStatus=143
Restart=always
RestartSec=5

# Hardening basico
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=/tmp

[Install]
WantedBy=multi-user.target
EOF
```

Ative e suba o servico:

```bash
sudo systemctl daemon-reload
sudo systemctl enable rick-api
sudo systemctl start rick-api
sudo systemctl status rick-api --no-pager
```

## 8) Verificacao e logs

Saude do processo:

```bash
sudo systemctl status rick-api --no-pager
sudo journalctl -u rick-api -f
```

Teste da API (na propria EC2):

```bash
curl -i http://localhost:8080/api/swagger-ui.html
curl -i http://localhost:8080/api/categorias
```

## 9) Update de versao (rolling simples)

```bash
scp -i /caminho/chave.pem target/api-agendamento-servicos-0.0.1-SNAPSHOT.jar ec2-user@SEU_IP_OU_DNS:/tmp/app.jar
ssh -i /caminho/chave.pem ec2-user@SEU_IP_OU_DNS
sudo systemctl stop rick-api
sudo mv /tmp/app.jar /opt/rick-api/app.jar
sudo chown rickapi:rickapi /opt/rick-api/app.jar
sudo chmod 540 /opt/rick-api/app.jar
sudo systemctl start rick-api
sudo systemctl status rick-api --no-pager
```

## 10) Troubleshooting rapido

- Erro de credencial/secret no boot:
  - confira `/etc/rick-api/rick-api.env`.
  - confira logs: `sudo journalctl -u rick-api -n 200 --no-pager`.
- Porta 8080 em uso:
  - `sudo ss -lntp | grep 8080`.
- Falha de conexao com MySQL/RabbitMQ:
  - valide `DB_URL`, `RABBITMQ_HOST` e Security Groups.

## Fontes confiaveis (oficiais)

- AWS EC2 User Guide: https://docs.aws.amazon.com/ec2/
- AWS Security Groups: https://docs.aws.amazon.com/vpc/latest/userguide/vpc-security-groups.html
- systemd.service man page: https://www.freedesktop.org/software/systemd/man/systemd.service.html
- Spring Boot Externalized Configuration: https://docs.spring.io/spring-boot/reference/features/external-config.html
- Spring Boot Deploying: https://docs.spring.io/spring-boot/reference/deployment/index.html

