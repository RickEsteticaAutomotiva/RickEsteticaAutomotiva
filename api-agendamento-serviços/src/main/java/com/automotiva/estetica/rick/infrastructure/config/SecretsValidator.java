package com.automotiva.estetica.rick.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * =====================================================
 * SecretsValidator — Validação de Secrets Críticos at Startup
 * =====================================================
 *
 * PROPÓSITO: Garantir que todos os secrets críticos estejam definidos
 * via variáveis de ambiente, em conformidade com OWASP A02: Cryptographic Failures
 *
 * COMPORTAMENTO:
 * - DEV: Apenas WARN se secret falta (permite continuar para debug local)
 * - HOMOLOG/PROD: ERROR + aborta startup se qualquer secret falta
 *
 * SECRETS VALIDADOS:
 * 1. JWT_SECRET — Autenticação JWT
 * 2. DB_PASSWORD — Banco de dados
 * 3. MAIL_PASSWORD — Serviço de email
 * 4. RABBITMQ_PASSWORD — Fila de mensagens
 *
 * @author GitHub Copilot
 * @since 2026-04-01
 */
@Configuration
public class SecretsValidator implements InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecretsValidator.class);

  @Value("${spring.profiles.active:dev}")
  private String activeProfiles;

  @Value("${JWT_SECRET:#{null}}")
  private String jwtSecret;

  @Value("${DB_PASSWORD:#{null}}")
  private String dbPassword;

  @Value("${MAIL_PASSWORD:#{null}}")
  private String mailPassword;

  @Value("${RABBITMQ_PASSWORD:#{null}}")
  private String rabbitmqPassword;

  private final Environment environment;

  public SecretsValidator(Environment environment) {
    this.environment = environment;
  }

  /**
   * InitializingBean hook — executado após a inicialização do contexto Spring,
   * mas antes da aplicação estar pronta.
   */
  @Override
  public void afterPropertiesSet() {
    LOGGER.info("[SecretsValidator] Iniciando validação de secrets críticos...");

    List<String> missingSecrets = new ArrayList<>();

    // Validar cada secret crítico
    if (isSecretMissing(jwtSecret)) {
      missingSecrets.add("JWT_SECRET");
    }
    if (isSecretMissing(dbPassword)) {
      missingSecrets.add("DB_PASSWORD");
    }
    if (isSecretMissing(mailPassword)) {
      missingSecrets.add("MAIL_PASSWORD");
    }
    if (isSecretMissing(rabbitmqPassword)) {
      missingSecrets.add("RABBITMQ_PASSWORD");
    }

    // Determinar nível de severidade
    boolean isProduction = isProduction();

    if (!missingSecrets.isEmpty()) {
      String message =
          String.format(
              """
              
              ❌ SEGURANÇA: Secrets não definidos (OWASP A02: Cryptographic Failures)
              
              Secrets faltando: %s
              
              Defina as variáveis de ambiente:
              
              export JWT_SECRET=<valor>
              export DB_PASSWORD=<valor>
              export MAIL_PASSWORD=<valor>
              export RABBITMQ_PASSWORD=<valor>
              
              DEV LOCAL: Você pode usar .env.local (gitignored)
              PRODUÇÃO: Use AWS Secrets Manager, Kubernetes Secrets ou similar
              
              Referência: SECRETS_DEPLOYMENT.md
              """,
              missingSecrets);

      if (isProduction) {
        LOGGER.error(message);
        throw new IllegalStateException(
            "❌ FALHA DE SEGURANÇA: Secrets críticos não definidos em PRODUÇÃO. Startup abortado.");
      } else {
        LOGGER.warn(message);
      }
    } else {
      LOGGER.info("✅ Todos os secrets críticos foram validados com sucesso!");
    }
  }

  /**
   * Verifica se um secret é válido (não nulo, não vazio, não placeholder).
   */
  private boolean isSecretMissing(String secretValue) {
    return secretValue == null
        || secretValue.isBlank()
        || secretValue.equals("placeholder")
        || secretValue.equals("{{null}}")
        || secretValue.equals("#{null}");
  }

  /**
   * Determina se a aplicação está em ambiente de produção.
   */
  private boolean isProduction() {
    String[] profiles = environment.getActiveProfiles();
    for (String profile : profiles) {
      if ("prod".equalsIgnoreCase(profile) || "production".equalsIgnoreCase(profile)) {
        return true;
      }
    }
    return false;
  }
}


