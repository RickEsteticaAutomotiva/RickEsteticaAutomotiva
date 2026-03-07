package com.automotiva.estetica.rick.adapter.in.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.automotiva.estetica.rick.application.dto.request.CarrinhoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("IT — CarrinhoController")
class CarrinhoControllerIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("GET /carrinhos/pessoa/{id} → 200 lista itens do carrinho")
    void listar_sucesso() throws Exception {
        // Pessoa 1 tem 1 item no carrinho (seed-it.sql)
        mockMvc.perform(
                        get(BASE_PATH + "/carrinhos/pessoa/1")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /carrinhos/pessoa/{id} → lista vazia quando carrinho está vazio")
    void listar_carrinhVazio() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/carrinhos/pessoa/9999")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /carrinhos/pessoa/{id} → 401 sem token")
    void listar_semToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/carrinhos/pessoa/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /carrinhos → 201 ao adicionar serviço ao carrinho")
    void adicionar_sucesso() throws Exception {
        CarrinhoRequest req = CarrinhoRequest.builder().idPessoa(2L).idServico(1L).build();

        mockMvc.perform(
                        post(BASE_PATH + "/carrinhos")
                                .header("Authorization", bearer(tokenAdmin))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /carrinhos → 400 quando campos obrigatórios ausentes")
    void adicionar_validacaoFalha() throws Exception {
        String json = "{\"idPessoa\":1}"; // idServico ausente

        mockMvc.perform(
                        post(BASE_PATH + "/carrinhos")
                                .header("Authorization", bearer(tokenAdmin))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /carrinhos/{id} → 204 ao remover item do carrinho")
    void remover_sucesso() throws Exception {
        // Item carrinho id=1 inserido no seed-it.sql
        mockMvc.perform(
                        delete(BASE_PATH + "/carrinhos/1")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /carrinhos/{id} → 404 quando item não existe")
    void remover_naoEncontrado() throws Exception {
        mockMvc.perform(
                        delete(BASE_PATH + "/carrinhos/9999")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /carrinhos/pessoa/{id}/limpar → 204 ao limpar carrinho da pessoa")
    void limpar_sucesso() throws Exception {
        mockMvc.perform(
                        delete(BASE_PATH + "/carrinhos/pessoa/1/limpar")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNoContent());
    }
}
