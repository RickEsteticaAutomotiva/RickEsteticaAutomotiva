package com.automotiva.estetica.rick.adapter.in.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("IT — OrdemServicoController")
class OrdemServicoControllerIT extends AbstractIntegrationTest {

    // ─── Listagem ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /ordem-servicos → 200 lista paginada autenticado")
    void buscarTodos_autenticado() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/ordem-servicos")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /ordem-servicos → 401 sem token")
    void buscarTodos_semToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /ordem-servicos/{id} → 200 ao buscar por ID existente")
    void buscarPorId_sucesso() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/ordem-servicos/1")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("GET /ordem-servicos/{id} → 404 quando ID não existe")
    void buscarPorId_naoEncontrado() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/ordem-servicos/9999")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /ordem-servicos/usuario/{id} → 200 lista por usuário")
    void buscarPorUsuario_sucesso() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/ordem-servicos/usuario/1")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /ordem-servicos/usuario/{id} → lista vazia para usuário sem ordens")
    void buscarPorUsuario_vazio() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/ordem-servicos/usuario/9999")
                                .header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ─── Criação ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /ordem-servicos → 201 ao criar nova ordem")
    void criar_sucesso() throws Exception {
        OrdemServicoRequest req =
                OrdemServicoRequest.builder()
                        .dataAgendamento(LocalDateTime.of(2026, 6, 15, 10, 0))
                        .veiculo(1L)
                        .precoMinimo(BigDecimal.valueOf(150))
                        .servicos(List.of(1L, 2L))
                        .observacoes("Teste de integração")
                        .build();

        mockMvc.perform(
                        post(BASE_PATH + "/ordem-servicos")
                                .header("Authorization", bearer(tokenAdmin))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName(
            "POST /ordem-servicos → 409 ao tentar criar ordem duplicada (mesmo veículo e data)")
    void criar_conflito() throws Exception {
        // Data/hora já inserida no seed-it.sql para o veículo 1
        OrdemServicoRequest req =
                OrdemServicoRequest.builder()
                        .dataAgendamento(LocalDateTime.of(2025, 12, 1, 10, 0))
                        .veiculo(1L)
                        .precoMinimo(BigDecimal.valueOf(150))
                        .build();

        mockMvc.perform(
                        post(BASE_PATH + "/ordem-servicos")
                                .header("Authorization", bearer(tokenAdmin))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /ordem-servicos → 400 quando campos obrigatórios ausentes")
    void criar_validacaoFalha() throws Exception {
        // dataAgendamento e veiculo são obrigatórios
        String json = "{\"precoMinimo\":100}";

        mockMvc.perform(
                        post(BASE_PATH + "/ordem-servicos")
                                .header("Authorization", bearer(tokenAdmin))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isBadRequest());
    }

    // ─── Atualização ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("PATCH /ordem-servicos/{id} → 200 ao atualizar ordem existente")
    void atualizar_sucesso() throws Exception {
        OrdemServicoRequest req =
                OrdemServicoRequest.builder()
                        .dataAgendamento(LocalDateTime.of(2025, 12, 1, 10, 0))
                        .veiculo(1L)
                        .precoMinimo(BigDecimal.valueOf(180))
                        .status(2L)
                        .observacoes("Atualizado via IT")
                        .build();

        mockMvc.perform(
                        patch(BASE_PATH + "/ordem-servicos/1")
                                .header("Authorization", bearer(tokenAdmin))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
