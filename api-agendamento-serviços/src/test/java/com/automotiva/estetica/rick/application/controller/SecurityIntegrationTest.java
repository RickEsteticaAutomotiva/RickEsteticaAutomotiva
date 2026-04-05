package com.automotiva.estetica.rick.application.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração para validar segurança de H2 e CORS.
 *
 * <p>
 * Executa com profile 'integration-test' que simula ambiente de produção.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@DisplayName("Testes de Integração — Segurança H2 e CORS")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * H2 Console em produção/test deve retornar 401 ou 403.
     *
     * <p>
     * Nota: Este teste valida que /h2-console/** não está em URLS_PUBLICAS no
     * perfil test.
     */
    @Test
    @DisplayName("POST /h2-console/ deve retornar 401 (não autenticado)")
    void testH2ConsoleRejectadoEmTest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/h2-console/")).andExpect(
                // Em test profile, H2 deve estar desabilitado ou requer autenticação
                // Esperamos 401 pois o SecurityConfig não permite acesso não autenticado
                status().isUnauthorized());
    }

    /**
     * Endpoint protegido sem token JWT deve retornar 401.
     */
    @Test
    @DisplayName("GET /ordem-servicos sem token deve retornar 401")
    void testEndpointProtegidoRequerJwt() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/ordem-servicos")).andExpect(status().isUnauthorized())
                .andExpect(status().reason("Acesso não autorizado"));
    }

    /**
     * GET para listagem de serviços deve ser público (sem autenticação).
     */
    @Test
    @DisplayName("GET /servicos sem token deve retornar 200 (endpoint público)")
    void testServicosEndpointPublico() throws Exception {
        // Act & Assert — /servicos está em URLS_PUBLICAS
        mockMvc.perform(get("/servicos")).andExpect(status().isOk());
    }

    /**
     * GET para listagem de categorias deve ser público (sem autenticação).
     */
    @Test
    @DisplayName("GET /categorias sem token deve retornar 200 (endpoint público)")
    void testCategoriasEndpointPublico() throws Exception {
        // Act & Assert — /categorias está em URLS_PUBLICAS
        mockMvc.perform(get("/categorias")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Headers de segurança devem estar presentes em endpoint público")
    void testSecurityHeadersPresentes() throws Exception {
        mockMvc.perform(get("/servicos")).andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"));
    }

    @Test
    @DisplayName("Endpoint protegido com token malformado deve retornar 401")
    void testTokenMalformadoRetorna401() throws Exception {
        mockMvc.perform(get("/ordem-servicos").header("Authorization", "Bearer token.invalido"))
                .andExpect(status().isUnauthorized()).andExpect(status().reason("Token invalido"));
    }

    @Test
    @DisplayName("Endpoint protegido com Bearer vazio deve retornar 401")
    void testBearerVazioRetorna401() throws Exception {
        mockMvc.perform(get("/ordem-servicos").header("Authorization", "Bearer ")).andExpect(status().isUnauthorized())
                .andExpect(status().reason("Token invalido"));
    }

    @Test
    @DisplayName("Endpoint protegido com token de assinatura invalida deve retornar 401")
    void testTokenAssinaturaInvalidaRetorna401() throws Exception {
        String tokenAssinaturaInvalida = gerarTokenComAssinaturaInvalida();

        mockMvc.perform(get("/ordem-servicos").header("Authorization", "Bearer " + tokenAssinaturaInvalida))
                .andExpect(status().isUnauthorized()).andExpect(status().reason("Token invalido"));
    }

    @Test
    @DisplayName("Endpoint protegido com token expirado deve retornar 401")
    void testTokenExpiradoRetorna401() throws Exception {
        String tokenExpirado = gerarTokenExpirado();

        mockMvc.perform(get("/ordem-servicos").header("Authorization", "Bearer " + tokenExpirado))
                .andExpect(status().isUnauthorized()).andExpect(status().reason("Token expirado"));
    }

    private String gerarTokenExpirado() {
        long agora = System.currentTimeMillis();
        Date emitidoEm = new Date(agora - 120_000);
        Date expiracao = new Date(agora - 60_000);

        return Jwts.builder().setSubject("token-expirado@teste.local").claim("roles", "ROLE_CLIENTE")
                .setIssuedAt(emitidoEm).setExpiration(expiracao)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .compact();
    }

    private String gerarTokenComAssinaturaInvalida() {
        long agora = System.currentTimeMillis();
        Date emitidoEm = new Date(agora - 5_000);
        Date expiracao = new Date(agora + 60_000);

        return Jwts.builder().setSubject("assinatura-invalida@teste.local").claim("roles", "ROLE_CLIENTE")
                .setIssuedAt(emitidoEm).setExpiration(expiracao)
                .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS512), SignatureAlgorithm.HS512).compact();
    }
}
