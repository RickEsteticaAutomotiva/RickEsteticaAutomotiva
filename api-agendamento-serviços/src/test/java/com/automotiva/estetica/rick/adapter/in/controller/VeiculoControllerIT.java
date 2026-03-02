package com.automotiva.estetica.rick.adapter.in.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.automotiva.estetica.rick.application.dto.request.VeiculoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("IT — VeiculoController")
class VeiculoControllerIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("GET /veiculos → 200 lista todos os veículos autenticado")
    void buscarTodos_autenticado() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/veiculos").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /veiculos → 401 sem token")
    void buscarTodos_semToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/veiculos")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /veiculos/pessoa/{id} → 200 lista veículos da pessoa")
    void buscarPorPessoa_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/veiculos/pessoa/1").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /veiculos/pessoa/{id} → lista vazia quando pessoa não tem veículos")
    void buscarPorPessoa_listaVazia() throws Exception {
        // pessoa 2 tem veículo DEF5678 no seed, mas testamos com ID inexistente
        mockMvc.perform(get(BASE_PATH + "/veiculos/pessoa/9999").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /veiculos → 201 ao cadastrar veículo")
    void cadastrar_sucesso() throws Exception {
        VeiculoRequest req = VeiculoRequest.builder()
                .idPessoa(2L)
                .placa("NEW9999")
                .modelo("Gol")
                .marca("Volkswagen")
                .porte("Pequeno")
                .cor("Verde")
                .ano("2023")
                .build();

        mockMvc.perform(post(BASE_PATH + "/veiculos")
                        .header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa", is("NEW9999")));
    }

    @Test
    @DisplayName("POST /veiculos → 400 quando campos obrigatórios ausentes")
    void cadastrar_validacaoFalha() throws Exception {
        String json = "{\"modelo\":\"Sem Placa\"}";

        mockMvc.perform(post(BASE_PATH + "/veiculos")
                        .header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /veiculos/{id} → 204 ao atualizar veículo")
    void atualizar_sucesso() throws Exception {
        VeiculoRequest req = VeiculoRequest.builder()
                .idPessoa(1L)
                .placa("ABC1234")
                .modelo("Civic Atualizado")
                .marca("Honda")
                .porte("Médio")
                .cor("Preto")
                .ano("2021")
                .build();

        mockMvc.perform(patch(BASE_PATH + "/veiculos/1")
                        .header("Authorization", bearer(tokenAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /veiculos/{id} → 204 e veículo não listado após soft-delete")
    void deletar_sucesso() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/veiculos/3").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNoContent());

        // Confirma que o veículo não aparece mais na listagem da pessoa (@SQLRestriction filtra deletado_em IS NOT NULL)
        mockMvc.perform(get(BASE_PATH + "/veiculos/pessoa/1").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", not(hasItem(3))));
    }

    @Test
    @DisplayName("DELETE /veiculos/{id} → 404 quando veículo não existe")
    void deletar_naoEncontrado() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/veiculos/9999").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNotFound());
    }
}
