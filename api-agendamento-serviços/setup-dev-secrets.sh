#!/bin/bash
# =====================================================
# setup-dev-secrets.sh — Setup de .env.local para DEV
# =====================================================
# PROPÓSITO: Criar arquivo .env.local (gitignored) com secrets
# de desenvolvimento local
#
# USO:
#   chmod +x setup-dev-secrets.sh
#   ./setup-dev-secrets.sh
#   # OU com valores customizados:
#   ./setup-dev-secrets.sh --db-pass "minha-senha" --jwt-secret "..."
# =====================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"

echo "🔒 OWASP A02: Setup de Secrets para Desenvolvimento Local"
echo "==========================================================="
echo ""

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Defaults compatíveis com docker local (JWT continua único por execução)
DB_PASSWORD=${DB_PASSWORD:-"rick@dev2024"}
JWT_SECRET=${JWT_SECRET:-"$(printf 'dev-jwt-%s-%s' "$(date +%s)" "$RANDOM" | base64 | tr -d '\n')"}
MAIL_PASSWORD=${MAIL_PASSWORD:-"test"}
RABBITMQ_PASSWORD=${RABBITMQ_PASSWORD:-"123456"}

# Parsing de argumentos
while [[ $# -gt 0 ]]; do
    case $1 in
        --db-pass)
            DB_PASSWORD="$2"
            shift 2
            ;;
        --jwt-secret)
            JWT_SECRET="$2"
            shift 2
            ;;
        --mail-pass)
            MAIL_PASSWORD="$2"
            shift 2
            ;;
        --rabbit-pass)
            RABBITMQ_PASSWORD="$2"
            shift 2
            ;;
        --help)
            echo "Uso: $0 [opções]"
            echo ""
            echo "Opções:"
            echo "  --db-pass         Senha do banco de dados (default: rick@dev2024)"
            echo "  --jwt-secret      JWT Secret em base64 (default: gerado automaticamente)"
            echo "  --mail-pass       Senha de email (default: test)"
            echo "  --rabbit-pass     Senha RabbitMQ (default: 123456)"
            echo "  --help            Mostra esta mensagem"
            exit 0
            ;;
        *)
            echo "Opção desconhecida: $1"
            exit 1
            ;;
    esac
done

# Arquivo de saída
ENV_LOCAL_FILE="$PROJECT_DIR/.env.local"

# Verificar se arquivo já existe
if [ -f "$ENV_LOCAL_FILE" ]; then
    echo -e "${YELLOW}⚠️  Arquivo $ENV_LOCAL_FILE já existe!${NC}"
    read -p "Deseja sobrescrever? (s/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Ss]$ ]]; then
        echo -e "${RED}Abortado.${NC}"
        exit 1
    fi
fi

# Criar arquivo
cat > "$ENV_LOCAL_FILE" << EOF
# =====================================================
# .env.local — Variáveis de Ambiente (Desenvolvimento Local)
# GERADO POR: setup-dev-secrets.sh em $(date)
# ARQUIVO GITIGNORED — Nunca será commitado
# =====================================================

# Database — MySQL Local
DB_URL=jdbc:mysql://localhost:3306/rick_dev?useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true&useSSL=false
DB_USERNAME=rick
DB_PASSWORD=${DB_PASSWORD}

# JWT Secret
JWT_SECRET=${JWT_SECRET}

# Email — MailHog para DEV local (docker compose)
MAIL_HOST=localhost
MAIL_PORT=1025
MAIL_USERNAME=test@test.com
MAIL_PASSWORD=${MAIL_PASSWORD}

# RabbitMQ — Docker container
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=${RABBITMQ_PASSWORD}

# CORS — Permissivo em DEV
ALLOWED_CORS_ORIGINS=*

# Spring Profiles
SPRING_PROFILES_ACTIVE=dev,swagger
EOF

chmod 600 "$ENV_LOCAL_FILE"

echo -e "${GREEN}✅ Arquivo criado com sucesso!${NC}"
echo ""
echo "   Localização: $ENV_LOCAL_FILE"
echo "   Permissões: 600 (read-write apenas para owner)"
echo "   Valores configurados:"
echo "   - DB_PASSWORD: ***"
echo "   - JWT_SECRET: ***"
echo "   - MAIL_PASSWORD: ***"
echo "   - RABBITMQ_PASSWORD: ***"
echo ""
echo -e "${YELLOW}⚠️  IMPORTANTE:${NC}"
echo "   Este arquivo está em .gitignore e NÃO será commitado"
echo "   Cada dev deve executar este script para ter seus próprios secrets"
echo ""
echo -e "${CYAN}📝 Próximos passos:${NC}"
echo ""
echo "1. Verificar Docker (MySQL + RabbitMQ):"
echo "   docker ps | grep -E 'mysql|rabbitmq'"
echo ""
echo "2. Se não estiverem rodando, iniciar:"
echo "   docker compose -f docker/docker-compose.yml up -d"
echo ""
echo "3. Carregar variáveis e compilar:"
echo "   source .env.local"
echo "   ./mvnw spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=dev,swagger'"
echo ""
echo "4. Testar:"
echo "   curl http://localhost:8080/api/swagger-ui.html"
echo ""
