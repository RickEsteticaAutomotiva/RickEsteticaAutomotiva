package com.automotiva.estetica.rick.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários da configuração de CORS e segurança profile-aware.
 *
 * <p>
 * Valida: - H2 Console acessível apenas em dev - CORS permissivo em dev,
 * restritivo em prod/homolog - Headers de segurança apropriados por profile
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de SecurityConfig — CORS e H2 Profile-Aware")
class SecurityConfigTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    @SuppressWarnings("unused")
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private Environment environment;

    @Mock
    private SecurityConfigProperties securityConfigProperties;

    @InjectMocks
    private SecurityConfig securityConfig;

    @SuppressWarnings("unchecked")
    private HeadersConfigurer<HttpSecurity> mockHeadersConfigurer() {
        return (HeadersConfigurer<HttpSecurity>) mock(HeadersConfigurer.class);
    }

    /**
     * CORS em dev deve permitir "*" (todas as origens).
     */
    @Test
    @DisplayName("CORS em dev deve permitir todas as origens")
    void testCorsPermitidoEmDev() {
        // Arrange — quando allowedOrigins é null, o código não chama outros getters
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev", "swagger"});
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
        CorsConfiguration config = source
                .getCorsConfiguration(new org.springframework.mock.web.MockHttpServletRequest("GET", "/test"));

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
        // Arrange — quando allowedOrigins é null, o código não chama
        // isAllowCredentials()
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
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
        CorsConfiguration config = source
                .getCorsConfiguration(new org.springframework.mock.web.MockHttpServletRequest("GET", "/test"));

        // Assert
        assertNotNull(config);
        assertNotNull(config.getAllowedOriginPatterns(), "allowedOriginPatterns não deve ser null em prod");
        assertTrue(config.getAllowedOriginPatterns().isEmpty(), "Em prod sem configuração, não deve permitir origens");
    }

    /**
     * CORS em prod com variable de ambiente deve usar as origens configuradas.
     */
    @Test
    @DisplayName("CORS em prod com env var deve usar domínios explícitos")
    void testCorsEmProdComEnvVar() {
        // Arrange — quando allowedOrigins NÃO é null, o código chama todos os getters
        java.util.List<String> allowedOrigins = java.util.List.of("https://app.example.com",
                "https://admin.example.com");
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
        CorsConfiguration config = source
                .getCorsConfiguration(new org.springframework.mock.web.MockHttpServletRequest("GET", "/test"));

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
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev", "swagger"});

        String[] urlsPublicas = ReflectionTestUtils.invokeMethod(securityConfig, "buildUrlsPublicas");

        assertNotNull(urlsPublicas);
        assertTrue(java.util.Arrays.asList(urlsPublicas).contains("/h2-console/**"));
    }

    /**
     * Em produção, H2 Console não deve estar acessível.
     */
    @Test
    @DisplayName("H2 Console não deve estar acessível em produção")
    void testH2ConsoleBloqueadoEmProd() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});

        String[] urlsPublicas = ReflectionTestUtils.invokeMethod(securityConfig, "buildUrlsPublicas");

        assertNotNull(urlsPublicas);
        assertFalse(java.util.Arrays.asList(urlsPublicas).contains("/h2-console/**"));
    }

    @Test
    @DisplayName("AuthenticationProvider deve autenticar credenciais válidas")
    void testAuthenticationProviderSucesso() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AuthenticationProvider provider = securityConfig.authenticationProvider(encoder);
        UserDetails user = User.withUsername("user@test.com").password("hash").authorities("ROLE_USER").build();

        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(user);
        when(encoder.matches("senha123", "hash")).thenReturn(true);

        var auth = provider.authenticate(new UsernamePasswordAuthenticationToken("user@test.com", "senha123"));

        assertNotNull(auth);
        assertEquals("user@test.com", auth.getName());
        assertNull(auth.getCredentials());
        assertTrue(auth.isAuthenticated());
    }

    @Test
    @DisplayName("AuthenticationProvider deve rejeitar credenciais inválidas")
    void testAuthenticationProviderFalhaCredenciais() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AuthenticationProvider provider = securityConfig.authenticationProvider(encoder);
        UserDetails user = User.withUsername("user@test.com").password("hash").authorities("ROLE_USER").build();

        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(user);
        when(encoder.matches("senhaErrada", "hash")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> provider.authenticate(new UsernamePasswordAuthenticationToken("user@test.com", "senhaErrada")));
    }

    @Test
    @DisplayName("AuthenticationProvider supports deve aceitar apenas UsernamePasswordAuthenticationToken")
    void testAuthenticationProviderSupports() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AuthenticationProvider provider = securityConfig.authenticationProvider(encoder);

        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
        assertFalse(provider.supports(org.springframework.security.authentication.TestingAuthenticationToken.class));
    }

    @Test
    @DisplayName("CORS com lista vazia explícita deve seguir fallback por profile")
    void testCorsComListaVaziaExplicita() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        when(securityConfigProperties.getAllowedOrigins()).thenReturn(java.util.List.of());
        when(securityConfigProperties.getAllowedMethods()).thenReturn(java.util.List.of("GET"));
        when(securityConfigProperties.getAllowedHeaders()).thenReturn(java.util.List.of("*"));
        when(securityConfigProperties.getExposedHeaders()).thenReturn(java.util.List.of("Authorization"));
        when(securityConfigProperties.getMaxAge()).thenReturn(1200L);

        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration config = source
                .getCorsConfiguration(new org.springframework.mock.web.MockHttpServletRequest("GET", "/test"));

        assertNotNull(config);
        assertNotNull(config.getAllowedOriginPatterns());
        assertTrue(config.getAllowedOriginPatterns().isEmpty());
        assertEquals(java.util.List.of("GET"), config.getAllowedMethods());
    }

    @Test
    @DisplayName("Deve detectar profile homolog no helper interno")
    void testIsHomologProfile() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"homolog"});

        Boolean isHomolog = ReflectionTestUtils.invokeMethod(securityConfig, "isHomologProfile");

        assertNotNull(isHomolog);
        assertTrue(isHomolog);
    }

    @Test
    @DisplayName("Deve retornar false para helper isDevProfile quando profile ativo nao for dev")
    void testIsDevProfileFalse() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});

        Boolean isDev = ReflectionTestUtils.invokeMethod(securityConfig, "isDevProfile");

        assertNotNull(isDev);
        assertFalse(isDev);
    }

    @Test
    @DisplayName("configureSecurityHeaders em dev deve pular HSTS")
    void testConfigureSecurityHeadersDev() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        HeadersConfigurer<HttpSecurity> headers = mockHeadersConfigurer();

        ReflectionTestUtils.invokeMethod(securityConfig, "configureSecurityHeaders", headers);

        verify(headers).frameOptions(any());
        verify(headers, never()).httpStrictTransportSecurity(any());
    }

    @Test
    @DisplayName("configureSecurityHeaders em homolog deve negar frame e pular HSTS")
    void testConfigureSecurityHeadersHomolog() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"homolog"});
        HeadersConfigurer<HttpSecurity> headers = mockHeadersConfigurer();

        ReflectionTestUtils.invokeMethod(securityConfig, "configureSecurityHeaders", headers);

        verify(headers).frameOptions(any());
        verify(headers, never()).httpStrictTransportSecurity(any());
    }

    @Test
    @DisplayName("configureSecurityHeaders em prod deve aplicar HSTS")
    void testConfigureSecurityHeadersProd() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        HeadersConfigurer<HttpSecurity> headers = mockHeadersConfigurer();

        ReflectionTestUtils.invokeMethod(securityConfig, "configureSecurityHeaders", headers);

        verify(headers).frameOptions(any());
        verify(headers).httpStrictTransportSecurity(any());
    }
}
