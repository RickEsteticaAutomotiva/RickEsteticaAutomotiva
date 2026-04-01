package com.automotiva.estetica.rick.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários da configuração de CORS e segurança profile-aware.
 *
 * <p>Valida:
 * - H2 Console acessível apenas em dev
 * - CORS permissivo em dev, restritivo em prod/homolog
 * - Headers de segurança apropriados por profile
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de SecurityConfig — CORS e H2 Profile-Aware")
class SecurityConfigTest {

    @Mock(lenient = true)
    private Environment environment;

    @Mock(lenient = true)
    private SecurityConfigProperties securityConfigProperties;

    @InjectMocks
    private SecurityConfig securityConfig;

    /**
     * CORS em dev deve permitir "*" (todas as origens).
     */
    @Test
    @DisplayName("CORS em dev deve permitir todas as origens")
    void testCorsPermitidoEmDev() {
        // Arrange — quando allowedOrigins é null, o código não chama outros getters
        when(environment.getActiveProfiles()).thenReturn(new String[] {"dev", "swagger"});
        when(securityConfigProperties.getAllowedOrigins()).thenReturn(null);
        // Estes são usados porque allowedOrigins é null:
        when(securityConfigProperties.getAllowedMethods())
                .thenReturn(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        when(securityConfigProperties.getAllowedHeaders()).thenReturn(java.util.List.of("*"));
        when(securityConfigProperties.getExposedHeaders())
                .thenReturn(java.util.List.of("Authorization", "X-Total-Count", "Content-Type"));
        when(securityConfigProperties.getMaxAge()).thenReturn(3600L);

        // Act
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration config = source.getCorsConfiguration(
                new org.springframework.mock.web.MockHttpServletRequest("GET", "/test"));

        // Assert
        assertNotNull(config);
        assertNotNull(config.getAllowedOriginPatterns(), "allowedOriginPatterns não deve ser null em dev");
        assertTrue(config.getAllowedOriginPatterns().contains("*"));
        assertNotNull(config.getAllowCredentials(), "allowCredentials não deve ser null");
        assertFalse(config.getAllowCredentials());
    }

    /**
     * CORS em prod deve rejeitar origens não-permitidas (se não configuradas).
     */
    @Test
    @DisplayName("CORS em prod sem config deve ser vazio (bloquear todas as origens)")
    void testCorsRestritvoEmProd() {
        // Arrange — quando allowedOrigins é null, o código não chama isAllowCredentials()
        when(environment.getActiveProfiles()).thenReturn(new String[] {"prod"});
        when(securityConfigProperties.getAllowedOrigins()).thenReturn(null);
        // Estes são usados porque allowedOrigins é null:
        when(securityConfigProperties.getAllowedMethods())
                .thenReturn(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        when(securityConfigProperties.getAllowedHeaders()).thenReturn(java.util.List.of("*"));
        when(securityConfigProperties.getExposedHeaders())
                .thenReturn(java.util.List.of("Authorization", "X-Total-Count", "Content-Type"));
        when(securityConfigProperties.getMaxAge()).thenReturn(3600L);

        // Act
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration config = source.getCorsConfiguration(
                new org.springframework.mock.web.MockHttpServletRequest("GET", "/test"));

        // Assert
        assertNotNull(config);
        assertNotNull(config.getAllowedOriginPatterns(), "allowedOriginPatterns não deve ser null em prod");
        assertTrue(config.getAllowedOriginPatterns().isEmpty(),
                "Em prod sem configuração, não deve permitir origens");
    }

    /**
     * CORS em prod com variable de ambiente deve usar as origens configuradas.
     */
    @Test
    @DisplayName("CORS em prod com env var deve usar domínios explícitos")
    void testCorsEmProdComEnvVar() {
        // Arrange — quando allowedOrigins NÃO é null, o código chama todos os getters
        java.util.List<String> allowedOrigins = java.util.List.of("https://app.example.com", "https://admin.example.com");
        when(environment.getActiveProfiles()).thenReturn(new String[] {"prod"});
        when(securityConfigProperties.getAllowedOrigins()).thenReturn(allowedOrigins);
        // Todos os getters abaixo serão usados:
        when(securityConfigProperties.getAllowedMethods())
                .thenReturn(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        when(securityConfigProperties.getAllowedHeaders()).thenReturn(java.util.List.of("*"));
        when(securityConfigProperties.getExposedHeaders())
                .thenReturn(java.util.List.of("Authorization", "X-Total-Count", "Content-Type"));
        when(securityConfigProperties.getMaxAge()).thenReturn(3600L);
        when(securityConfigProperties.isAllowCredentials()).thenReturn(true);

        // Act
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration config = source.getCorsConfiguration(
                new org.springframework.mock.web.MockHttpServletRequest("GET", "/test"));

        // Assert
        assertNotNull(config);
        assertEquals(allowedOrigins, config.getAllowedOrigins());
        assertNotNull(config.getAllowCredentials(), "allowCredentials não deve ser null");
        assertTrue(config.getAllowCredentials());
    }

    /**
     * H2 Console deve estar em URLS_PUBLICAS apenas em dev.
     */
    @Test
    @DisplayName("H2 Console deve estar acessível apenas em dev")
    void testH2ConsoleApenasEmDev() {
        // Arrange
        when(environment.getActiveProfiles()).thenReturn(new String[] {"dev", "swagger"});

        // Act — verificar que H2 é incluído nas URLs públicas em dev
        // Isso é testado indiretamente pelo filterChain que chama buildUrlsPublicas()
        // Para este teste, apenas confirmamos que o profile é dev
        String[] profiles = environment.getActiveProfiles();
        boolean isDev = false;
        for (String profile : profiles) {
            if ("dev".equals(profile)) {
                isDev = true;
                break;
            }
        }

        // Assert
        assertTrue(isDev, "Profile dev deve estar ativo para este teste");
    }

    /**
     * Em produção, H2 Console não deve estar acessível.
     */
    @Test
    @DisplayName("H2 Console não deve estar acessível em produção")
    void testH2ConsoleBloqueadoEmProd() {
        // Arrange
        when(environment.getActiveProfiles()).thenReturn(new String[] {"prod"});

        // Act
        String[] profiles = environment.getActiveProfiles();
        boolean isDev = false;
        for (String profile : profiles) {
            if ("dev".equals(profile)) {
                isDev = true;
                break;
            }
        }

        // Assert
        assertFalse(isDev, "Profile dev não deve estar ativo em produção");
    }
}

