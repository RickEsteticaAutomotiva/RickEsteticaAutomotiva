package com.automotiva.estetica.rick.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Valida presença de secrets críticos no startup conforme OWASP A02. Em dev
 * apenas registra WARN; em homolog/prod/staging interrompe a aplicação.
 */
@Configuration
public class SecretsValidator implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecretsValidator.class);

    private static final List<String> INVALID_SECRET_VALUES = List.of("placeholder", "{{null}}", "#{null}", "test",
            "123456", "rick@dev2024");

    @Value("${jwt.secret:#{null}}")
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
        boolean isRestrictedEnvironment = isRestrictedEnvironment();

        if (!missingSecrets.isEmpty()) {
            String message = String.format("""

                    ❌ SEGURANÇA: Secrets não definidos (OWASP A02: Cryptographic Failures)

                    Secrets faltando: %s

                    Defina as variáveis de ambiente:

                    export JWT_SECRET=<valor>
                    export DB_PASSWORD=<valor>
                    export MAIL_PASSWORD=<valor>
                    export RABBITMQ_PASSWORD=<valor>

                    DEV LOCAL: Você pode usar .env.local (gitignored)
                    PRODUÇÃO/HOMOLOG: Use AWS Secrets Manager, Kubernetes Secrets ou similar
                    """, missingSecrets);

            if (isRestrictedEnvironment) {
                LOGGER.error(message);
                throw new IllegalStateException(
                        "❌ FALHA DE SEGURANÇA: Secrets críticos não definidos em ambiente restrito. Startup abortado.");
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
        if (secretValue == null || secretValue.isBlank()) {
            return true;
        }
        String normalizedValue = secretValue.trim();
        for (String invalidValue : INVALID_SECRET_VALUES) {
            if (invalidValue.equalsIgnoreCase(normalizedValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determina se a aplicação está em ambiente restrito (homolog/prod).
     */
    private boolean isRestrictedEnvironment() {
        String[] profiles = environment.getActiveProfiles();
        for (String profile : profiles) {
            if ("prod".equalsIgnoreCase(profile) || "production".equalsIgnoreCase(profile)
                    || "homolog".equalsIgnoreCase(profile) || "staging".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }
}
