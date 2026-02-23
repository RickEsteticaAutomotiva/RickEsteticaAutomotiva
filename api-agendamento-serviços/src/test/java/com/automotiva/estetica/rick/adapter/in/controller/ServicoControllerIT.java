package com.automotiva.estetica.rick.adapter.in.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.automotiva.estetica.rick.application.dto.request.ServicoRequest;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("IT — ServicoController")
class ServicoControllerIT extends AbstractIntegrationTest {

    // ─── Público ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /servicos → 200 sem autenticação (endpoint público)")
    void buscarTodos_publico() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /servicos/{id} → 200 sem autenticação")
    void buscarPorId_publico_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/servicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Lavagem Simples")));
    }

    @Test
    @DisplayName("GET /servicos/{id} → 404 quando serviço não existe")
    void buscarPorId_naoEncontrado() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/servicos/9999")).andExpect(status().isNotFound());
    }

    // ─── Protegido ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /servicos → 201 ao criar serviço autenticado")
    void criar_sucesso() throws Exception {
        ServicoRequest req = ServicoRequest.builder()
                .nome("Lavagem Premium IT")
                .descricao("Lavagem ultra premium")
                .preco(BigDecimal.valueOf(99.90))
                .categoriaId(1L)
                .build();

        mockMvc.perform(post(BASE_PATH + "/servicos")
                        .header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Lavagem Premium IT")))
                .andExpect(jsonPath("$.preco", is(99.90)));
    }

    @Test
    @DisplayName("POST /servicos → 401 sem autenticação")
    void criar_semToken() throws Exception {
        ServicoRequest req = ServicoRequest.builder()
                .nome("Sem Auth")
                .preco(BigDecimal.valueOf(10))
                .categoriaId(1L)
                .build();

        mockMvc.perform(post(BASE_PATH + "/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /servicos → 400 quando campos obrigatórios ausentes")
    void criar_validacaoFalha() throws Exception {
        // preco e categoriaId obrigatórios
        String json = "{\"nome\":\"Sem Campos\"}";

        mockMvc.perform(post(BASE_PATH + "/servicos")
                        .header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /servicos/{id} → 200 ao atualizar serviço")
    void atualizar_sucesso() throws Exception {
        ServicoRequest req = ServicoRequest.builder()
                .nome("Lavagem Simples Atualizada")
                .preco(BigDecimal.valueOf(30.00))
                .categoriaId(1L)
                .build();

        mockMvc.perform(patch(BASE_PATH + "/servicos/1")
                        .header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Lavagem Simples Atualizada")));
    }

    @Test
    @DisplayName("DELETE /servicos/{id} → 204 ao remover serviço")
    void deletar_sucesso() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/servicos/5").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /servicos/{id} → 404 quando serviço não existe")
    void deletar_naoEncontrado() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/servicos/9999").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNotFound());
    }
}
