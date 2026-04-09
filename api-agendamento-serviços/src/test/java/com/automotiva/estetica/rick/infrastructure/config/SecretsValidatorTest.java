package com.automotiva.estetica.rick.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de SecretsValidator")
class SecretsValidatorTest {

    @Mock
    private Environment environment;

    private SecretsValidator secretsValidator;

    @BeforeEach
    void setUp() {
        secretsValidator = new SecretsValidator(environment);
        ReflectionTestUtils.setField(secretsValidator, "jwtSecret", "jwt-secret");
        ReflectionTestUtils.setField(secretsValidator, "dbPassword", "db-password");
        ReflectionTestUtils.setField(secretsValidator, "mailPassword", "mail-password");
        ReflectionTestUtils.setField(secretsValidator, "rabbitmqPassword", "rabbit-password");
    }

    @Test
    @DisplayName("deve falhar startup em prod quando faltar secret")
    void afterPropertiesSet_deveFalharEmProdQuandoFaltarSecret() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        ReflectionTestUtils.setField(secretsValidator, "mailPassword", " ");

        assertThrows(IllegalStateException.class, () -> secretsValidator.afterPropertiesSet());
    }

    @Test
    @DisplayName("deve permitir startup em dev quando faltar secret")
    void afterPropertiesSet_devePermitirEmDevQuandoFaltarSecret() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        ReflectionTestUtils.setField(secretsValidator, "jwtSecret", " ");

        assertDoesNotThrow(() -> secretsValidator.afterPropertiesSet());
    }

    @Test
    @DisplayName("deve validar sucesso quando todos secrets existirem")
    void afterPropertiesSet_deveValidarQuandoTodosSecretsExistirem() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"production"});

        assertDoesNotThrow(() -> secretsValidator.afterPropertiesSet());
    }

    @Test
    @DisplayName("deve tratar placeholders como secret ausente em prod")
    void afterPropertiesSet_deveTratarPlaceholderComoAusente() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        ReflectionTestUtils.setField(secretsValidator, "rabbitmqPassword", "#{null}");

        assertThrows(IllegalStateException.class, () -> secretsValidator.afterPropertiesSet());
    }

    @Test
    @DisplayName("deve falhar em homolog quando usar secret fraco conhecido")
    void afterPropertiesSet_deveFalharEmHomologQuandoSecretFraco() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"homolog"});
        ReflectionTestUtils.setField(secretsValidator, "rabbitmqPassword", "123456");

        assertThrows(IllegalStateException.class, () -> secretsValidator.afterPropertiesSet());
    }

    @Test
    @DisplayName("deve tratar {{null}} como secret ausente em prod")
    void afterPropertiesSet_deveTratarDoubleBracesNullComoAusente() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        ReflectionTestUtils.setField(secretsValidator, "dbPassword", "{{null}}");

        assertThrows(IllegalStateException.class, () -> secretsValidator.afterPropertiesSet());
    }

    @Test
    @DisplayName("helper isSecretMissing deve identificar placeholders e vazios")
    void isSecretMissing_deveIdentificarValoresInvalidos() {
        Boolean missingNull = ReflectionTestUtils.invokeMethod(secretsValidator, "isSecretMissing", (String) null);
        Boolean missingBlank = ReflectionTestUtils.invokeMethod(secretsValidator, "isSecretMissing", " ");
        Boolean missingPlaceholder = ReflectionTestUtils.invokeMethod(secretsValidator, "isSecretMissing",
                "placeholder");
        Boolean missingWeakDefault = ReflectionTestUtils.invokeMethod(secretsValidator, "isSecretMissing", "test");
        Boolean validSecret = ReflectionTestUtils.invokeMethod(secretsValidator, "isSecretMissing", "abc123@Forte");

        assertEquals(Boolean.TRUE, missingNull);
        assertEquals(Boolean.TRUE, missingBlank);
        assertEquals(Boolean.TRUE, missingPlaceholder);
        assertEquals(Boolean.TRUE, missingWeakDefault);
        assertNotEquals(Boolean.TRUE, validSecret);
    }

    @Test
    @DisplayName("helper isRestrictedEnvironment deve aceitar production em qualquer caixa")
    void isRestrictedEnvironment_deveAceitarCaseInsensitive() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"PrOdUcTiOn"});

        Boolean result = ReflectionTestUtils.invokeMethod(secretsValidator, "isRestrictedEnvironment");

        assertEquals(Boolean.TRUE, result);
    }

    @Test
    @DisplayName("helper isRestrictedEnvironment deve retornar false quando profile nao for restrito")
    void isRestrictedEnvironment_deveRetornarFalseParaDev() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});

        Boolean result = ReflectionTestUtils.invokeMethod(secretsValidator, "isRestrictedEnvironment");

        assertNotEquals(Boolean.TRUE, result);
    }

    @Test
    @DisplayName("deve permitir startup quando sem profile de producao")
    void afterPropertiesSet_devePermitirQuandoSemProfileProd() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{});
        ReflectionTestUtils.setField(secretsValidator, "jwtSecret", "placeholder");

        assertDoesNotThrow(() -> secretsValidator.afterPropertiesSet());
    }
}
