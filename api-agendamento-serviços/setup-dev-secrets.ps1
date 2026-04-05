# =====================================================
# setup-dev-secrets.ps1 — Setup de .env.local para DEV
# =====================================================
# PROPOSITO: Criar arquivo .env.local (gitignored) com secrets
# de desenvolvimento local
#
# USO: .\setup-dev-secrets.ps1
# =====================================================

param(
    [string]$DbPassword = "",
    [string]$JwtSecret = "",
    [string]$MailPassword = "",
    [string]$RabbitmqPassword = "",
    [switch]$Force
)

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectDir = $ScriptDir
$EnvLocalFile = Join-Path $ProjectDir ".env.local"

if ([string]::IsNullOrWhiteSpace($DbPassword)) {
    $DbPassword = "rick@dev2024"
}
if ([string]::IsNullOrWhiteSpace($JwtSecret)) {
    $jwtBytes = New-Object byte[] 48
    [System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($jwtBytes)
    $JwtSecret = [Convert]::ToBase64String($jwtBytes)
}
if ([string]::IsNullOrWhiteSpace($MailPassword)) {
    $MailPassword = "test"
}
if ([string]::IsNullOrWhiteSpace($RabbitmqPassword)) {
    $RabbitmqPassword = "123456"
}

Write-Host "OWASP A02: Setup de Secrets para Desenvolvimento Local" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host ""

if ((Test-Path $EnvLocalFile) -and -not $Force) {
    Write-Host "AVISO: Arquivo $EnvLocalFile ja existe!" -ForegroundColor Yellow
    $response = Read-Host "Deseja sobrescrever? (s/n)"
    if ($response -ne 's' -and $response -ne 'S') {
        Write-Host "Abortado." -ForegroundColor Red
        exit 1
    }
}

$content = @"
# =====================================================
# .env.local — Variáveis de Ambiente (Desenvolvimento Local)
# GERADO POR: setup-dev-secrets.ps1 em $(Get-Date)
# ARQUIVO GITIGNORED — Nunca será commitado
# =====================================================

# Database — MySQL Local
DB_URL=jdbc:mysql://localhost:3306/rick_dev?useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true&useSSL=false
DB_USERNAME=rick
DB_PASSWORD=$DbPassword

# JWT Secret
JWT_SECRET=$JwtSecret

# Email — MailHog para DEV local (docker compose)
MAIL_HOST=localhost
MAIL_PORT=1025
MAIL_USERNAME=test@test.com
MAIL_PASSWORD=$MailPassword

# RabbitMQ — Docker container
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=$RabbitmqPassword

# CORS — Permissivo em DEV
ALLOWED_CORS_ORIGINS=*

# Spring Profiles
SPRING_PROFILES_ACTIVE=dev,swagger
"@

Set-Content -Path $EnvLocalFile -Value $content -Force

Write-Host "SUCESSO: Arquivo criado com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "   Localizacao: $EnvLocalFile"
Write-Host "   Valores configurados:"
Write-Host "   - DB_PASSWORD: ***"
Write-Host "   - JWT_SECRET: ***"
Write-Host "   - MAIL_PASSWORD: ***"
Write-Host "   - RABBITMQ_PASSWORD: ***"
Write-Host ""
Write-Host "IMPORTANTE:" -ForegroundColor Yellow
Write-Host "   Este arquivo esta em .gitignore e NAO sera commitado"
Write-Host "   Cada dev deve executar este script para ter seus proprios secrets"
Write-Host ""
Write-Host "Proximos passos:" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Verificar Docker (MySQL + RabbitMQ):"
Write-Host "   docker ps | Select-String mysql,rabbitmq"
Write-Host ""
Write-Host "2. Se nao estiverem rodando, iniciar:"
Write-Host "   docker compose -f docker/docker-compose.yml up -d"
Write-Host ""
Write-Host "3. Compilar e rodar a aplicacao:"
$mavenCmd = ".\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=`"--spring.profiles.active=dev,swagger`""
Write-Host "   $mavenCmd"
Write-Host ""
Write-Host "4. Testar:"
Write-Host "   curl http://localhost:8080/api/swagger-ui.html"
Write-Host ""
