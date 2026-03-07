package com.automotiva.estetica.rick.adapter.in.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.automotiva.estetica.rick.application.dto.request.FavoritoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("IT — FavoritoController")
class FavoritoControllerIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("GET /favoritos/pessoa/{id} → 200 lista favoritos da pessoa")
    void listar_sucesso() throws Exception {
        // Pessoa 1 tem 1 favorito (seed-it.sql)
        mockMvc.perform(
                        get(BASE_PATH + "/favoritos/pessoa/1")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /favoritos/pessoa/{id} → lista vazia para pessoa sem favoritos")
    void listar_vazio() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/favoritos/pessoa/9999")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /favoritos/pessoa/{id} → 401 sem token")
    void listar_semToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/favoritos/pessoa/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /favoritos → 201 ao adicionar serviço aos favoritos")
    void adicionar_sucesso() throws Exception {
        FavoritoRequest req = FavoritoRequest.builder().idPessoa(2L).idServico(2L).build();

        mockMvc.perform(
                        post(BASE_PATH + "/favoritos")
                                .header("Authorization", bearer(tokenAdmin))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /favoritos → 400 quando campos obrigatórios ausentes")
    void adicionar_validacaoFalha() throws Exception {
        String json = "{\"idPessoa\":1}"; // idServico ausente

        mockMvc.perform(
                        post(BASE_PATH + "/favoritos")
                                .header("Authorization", bearer(tokenAdmin))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /favoritos/{id} → 204 ao remover favorito")
    void remover_sucesso() throws Exception {
        // Favorito id=1 inserido no seed-it.sql
        mockMvc.perform(
                        delete(BASE_PATH + "/favoritos/1")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /favoritos/{id} → 404 quando favorito não existe")
    void remover_naoEncontrado() throws Exception {
        mockMvc.perform(
                        delete(BASE_PATH + "/favoritos/9999")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNotFound());
    }
}
