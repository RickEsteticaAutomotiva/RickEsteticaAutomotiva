package com.automotiva.estetica.rick.adapter.in.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.automotiva.estetica.rick.application.dto.request.CategoriaRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("IT — CategoriaController")
class CategoriaControllerIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("GET /categorias → 200 sem autenticação (endpoint público)")
    void buscarTodas_publico() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/categorias")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nome", not(emptyString())));
    }

    @Test
    @DisplayName("POST /categorias → 201 ao criar categoria autenticado")
    void criar_sucesso() throws Exception {
        CategoriaRequest req = CategoriaRequest.builder().nome("Higienização IT").build();

        mockMvc.perform(post(BASE_PATH + "/categorias").header("Authorization", bearer(tokenAdmin))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /categorias → 400 quando nome está em branco")
    void criar_nomeVazio() throws Exception {
        CategoriaRequest req = CategoriaRequest.builder().nome("").build();

        mockMvc.perform(post(BASE_PATH + "/categorias").header("Authorization", bearer(tokenAdmin))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /categorias → 401 sem autenticação")
    void criar_semToken() throws Exception {
        CategoriaRequest req = CategoriaRequest.builder().nome("Sem Auth").build();

        mockMvc.perform(post(BASE_PATH + "/categorias").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /categorias/{id} → 200 ao atualizar categoria")
    void atualizar_sucesso() throws Exception {
        CategoriaRequest req = CategoriaRequest.builder().nome("Lavagem Atualizada IT").build();

        mockMvc.perform(patch(BASE_PATH + "/categorias/1").header("Authorization", bearer(tokenAdmin))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome", is("Lavagem Atualizada IT")));
    }

    @Test
    @DisplayName("PATCH /categorias/{id} → 404 quando categoria não existe")
    void atualizar_naoEncontrada() throws Exception {
        CategoriaRequest req = CategoriaRequest.builder().nome("Inexistente").build();

        mockMvc.perform(patch(BASE_PATH + "/categorias/9999").header("Authorization", bearer(tokenAdmin))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }
}
