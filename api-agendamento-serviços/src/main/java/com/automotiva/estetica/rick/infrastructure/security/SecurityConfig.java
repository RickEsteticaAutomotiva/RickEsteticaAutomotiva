package com.automotiva.estetica.rick.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuração centralizada de segurança do Spring Security.
 *
 * <p>
 * Responsável por: - Autenticação via JWT - CORS profile-aware (dev permite
 * "*", prod usa env vars) - H2 Console apenas em dev - Headers de segurança
 * (X-Frame-Options, X-Content-Type-Options, HSTS) - Autorização stateless
 * (SessionCreationPolicy.STATELESS)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final SecurityConfigProperties securityConfigProperties;
    private final Environment environment;

    // URLs públicas que não requerem autenticação (exceto H2 que é profile-aware)
    private static final String[] URLS_PUBLICAS_BASE = {"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
            "/swagger-resources/**", "/webjars/**", "/pessoas/login", "/pessoas/", "/servicos", "/servicos/**",
            "/categorias", "/error"};

    /**
     * Constrói array de URLs públicas incluindo H2 apenas se profile for dev.
     */
    private String[] buildUrlsPublicas() {
        List<String> urls = new ArrayList<>();
        for (String url : URLS_PUBLICAS_BASE) {
            urls.add(url);
        }
        // Incluir /h2-console/** apenas se profile for 'dev'
        if (isDevProfile()) {
            urls.add("/h2-console/**");
        }
        return urls.toArray(new String[0]);
    }

    /**
     * Verifica se o profile ativo é 'dev'.
     */
    private boolean isDevProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("dev".equals(profile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se o profile ativo é 'homolog'.
     */
    private boolean isHomologProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("homolog".equals(profile)) {
                return true;
            }
        }
        return false;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder encoder) {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
                if (!encoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
                    throw new BadCredentialsException("E-mail ou senha incorretos");
                }
                return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return authentication.equals(UsernamePasswordAuthenticationToken.class);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, AuthenticationProvider authProvider)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).authenticationProvider(authProvider).build();
    }

    /**
     * SecurityFilterChain com configurações profile-aware para headers, CORS e H2.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> response
                                .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acesso não autorizado"))
                        .accessDeniedHandler((request, response, accessDeniedException) -> response
                                .sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado")))
                .authorizeHttpRequests(auth -> auth.requestMatchers(buildUrlsPublicas()).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(this::configureSecurityHeaders);

        return http.build();
    }

    /**
     * Configura headers de segurança de forma profile-aware.
     *
     * <p>
     * - X-Frame-Options: DENY em prod/homolog, SAMEORIGIN em dev (para H2) -
     * X-Content-Type-Options: nosniff em todos (padrão Spring) -
     * Strict-Transport-Security: apenas em prod
     */
    private void configureSecurityHeaders(
            org.springframework.security.config.annotation.web.configurers.HeadersConfigurer<?> headers) {
        if (isDevProfile()) {
            // Em dev, permitir frames (necessário para H2 console)
            headers.frameOptions(fo -> fo.sameOrigin());
        } else {
            // Em produção/homolog, headers de segurança rigorosos
            headers.frameOptions(fo -> fo.deny());

            // Adicionar HSTS apenas em prod
            if (!isHomologProfile()) {
                headers.httpStrictTransportSecurity(
                        hsts -> hsts.maxAgeInSeconds(31536000).includeSubDomains(true).preload(true));
            }
        }
    }

    /**
     * CorsConfigurationSource que respeita o profile ativo.
     *
     * <p>
     * - dev: permite "*" (todas as origens) - homolog/prod: usa variável de
     * ambiente ALLOWED_CORS_ORIGINS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Se nenhuma origem foi configurada, usar padrão baseado no profile
        List<String> allowedOrigins = securityConfigProperties.getAllowedOrigins();
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            if (isDevProfile()) {
                // Em dev, permitir todas as origens (o proxy filtrará em homolog/prod)
                config.setAllowedOriginPatterns(List.of("*"));
                config.setAllowCredentials(false);
            } else {
                // Em produção, ser mais restritivo por padrão
                // Recomendado: configurar ALLOWED_CORS_ORIGINS via env var
                config.setAllowedOriginPatterns(List.of());
                config.setAllowCredentials(false);
            }
        } else {
            config.setAllowedOrigins(allowedOrigins);
            config.setAllowCredentials(securityConfigProperties.isAllowCredentials());
        }

        config.setAllowedMethods(securityConfigProperties.getAllowedMethods());
        config.setAllowedHeaders(securityConfigProperties.getAllowedHeaders());
        config.setExposedHeaders(securityConfigProperties.getExposedHeaders());
        config.setMaxAge(securityConfigProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
