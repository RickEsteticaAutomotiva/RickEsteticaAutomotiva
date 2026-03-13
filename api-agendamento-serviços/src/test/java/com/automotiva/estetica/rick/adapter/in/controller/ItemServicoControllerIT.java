package com.automotiva.estetica.rick.adapter.in.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("IT — ItemServicoController")
class ItemServicoControllerIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("GET /itens-servico → 200 lista todos os itens autenticado")
    void buscarTodos_autenticado() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/itens-servico").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /itens-servico → 401 sem token")
    void buscarTodos_semToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/itens-servico")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /itens-servico/{id} → 200 ao buscar item existente")
    void buscarPorId_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/itens-servico/1").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("GET /itens-servico/{id} → 404 quando item não existe")
    void buscarPorId_naoEncontrado() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/itens-servico/9999").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /itens-servico/ordem/{id} → 200 lista itens da ordem")
    void listarPorOrdem_sucesso() throws Exception {
        // Ordem 1 tem 2 itens no seed-it.sql
        mockMvc.perform(get(BASE_PATH + "/itens-servico/ordem/1").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /itens-servico/ordem/{id} → lista vazia para ordem inexistente")
    void listarPorOrdem_vazio() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/itens-servico/ordem/9999").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }
}
