package com.automotiva.estetica.rick.adapter.in.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração para validar segurança de H2 e CORS.
 *
 * <p>Executa com profile 'integration-test' que simula ambiente de produção.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@DisplayName("Testes de Integração — Segurança H2 e CORS")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * H2 Console em produção/test deve retornar 401 ou 403.
     *
     * <p>Nota: Este teste valida que /h2-console/** não está em URLS_PUBLICAS no perfil test.
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
        mockMvc.perform(get("/ordem-servicos"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * GET para listagem de serviços deve ser público (sem autenticação).
     */
    @Test
    @DisplayName("GET /servicos sem token deve retornar 200 (endpoint público)")
    void testServicosEndpointPublico() throws Exception {
        // Act & Assert — /servicos está em URLS_PUBLICAS
        mockMvc.perform(get("/servicos"))
                .andExpect(status().isOk());
    }

    /**
     * GET para listagem de categorias deve ser público (sem autenticação).
     */
    @Test
    @DisplayName("GET /categorias sem token deve retornar 200 (endpoint público)")
    void testCategoriasEndpointPublico() throws Exception {
        // Act & Assert — /categorias está em URLS_PUBLICAS
        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk());
    }
}

