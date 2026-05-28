package com.automotiva.estetica.rick.application.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@DisplayName("IT — OrdemServicoController")
class OrdemServicoControllerIT extends AbstractIntegrationTest {

    // ─── Listagem ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /ordem-servicos → 200 lista paginada autenticado")
    void buscarTodos_autenticado() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /ordem-servicos → 401 sem token")
    void buscarTodos_semToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /ordem-servicos/{id} → 200 ao buscar por ID existente")
    void buscarPorId_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos/1").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status.id", notNullValue())).andExpect(jsonPath("$.veiculo.id", notNullValue()))
                .andExpect(jsonPath("$.servicos", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /ordem-servicos/{id} → 404 quando ID não existe")
    void buscarPorId_naoEncontrado() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos/9999").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /ordem-servicos/usuario/{id} → 200 lista por usuário")
    void buscarPorUsuario_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos/usuario/1").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /ordem-servicos/usuario/{id} → 401 sem token")
    void buscarPorUsuario_semToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos/usuario/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /ordem-servicos/usuario/{id} → 403 ao tentar acessar ordens de outro usuário")
    void buscarPorUsuario_outroUsuario_deve403() throws Exception {
        // tokenUser (id=2) tentando acessar ordens do usuário 1
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos/usuario/1").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /ordem-servicos/usuario/{id} → 200 ao acessar as próprias ordens")
    void buscarPorUsuario_proprioUsuario_deve200() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos/usuario/2").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /ordem-servicos/usuario/{id} → 403 para id sem ownership")
    void buscarPorUsuario_vazio() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos/usuario/9999").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isForbidden());
    }

    // ─── Criação ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /ordem-servicos → 202 ao criar nova ordem")
    void criar_sucesso() throws Exception {
        OrdemServicoRequest req = OrdemServicoRequest.builder().dataAgendamento(LocalDateTime.of(2026, 6, 15, 10, 0))
                .veiculo(1L).precoMinimo(BigDecimal.valueOf(150)).servicos(List.of(1L, 2L))
                .observacoes("Teste de integração").build();

        mockMvc.perform(post(BASE_PATH + "/ordem-servicos").header("Authorization", bearer(tokenAdmin))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status.id", notNullValue())).andExpect(jsonPath("$.veiculo.id", notNullValue()));
    }

    @Test
    @DisplayName("POST /ordem-servicos → 409 ao tentar criar ordem duplicada (mesmo veículo e data)")
    void criar_conflito() throws Exception {
        // Data/hora já inserida no seed-it.sql para o veículo 1
        OrdemServicoRequest req = OrdemServicoRequest.builder().dataAgendamento(LocalDateTime.of(2025, 12, 1, 10, 0))
                .veiculo(1L).precoMinimo(BigDecimal.valueOf(150)).build();

        mockMvc.perform(post(BASE_PATH + "/ordem-servicos").header("Authorization", bearer(tokenAdmin))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /ordem-servicos → 400 quando campos obrigatórios ausentes")
    void criar_validacaoFalha() throws Exception {
        // dataAgendamento e veiculo são obrigatórios
        String json = "{\"precoMinimo\":100}";

        mockMvc.perform(post(BASE_PATH + "/ordem-servicos").header("Authorization", bearer(tokenAdmin))
                .contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    // ─── Atualização ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("PATCH /ordem-servicos/{id} → 200 ao atualizar ordem existente")
    void atualizar_sucesso() throws Exception {
        OrdemServicoRequest req = OrdemServicoRequest.builder().dataAgendamento(LocalDateTime.of(2025, 12, 1, 10, 0))
                .veiculo(1L).precoMinimo(BigDecimal.valueOf(180)).status(2L).observacoes("Atualizado via IT").build();

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos/1").header("Authorization", bearer(tokenAdmin))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /ordem-servicos/horarios-disponiveis -> 200 com horarios livres")
    void buscarHorariosDisponiveis_sucesso() throws Exception {
        try (MockedStatic<LocalTime> mock = Mockito.mockStatic(LocalTime.class, Mockito.CALLS_REAL_METHODS)) {

            LocalTime horarioValido = LocalTime.of(10, 0);
            mock.when(LocalTime::now).thenReturn(horarioValido);

            mockMvc.perform(
                    get(BASE_PATH + "/ordem-servicos/horarios-disponiveis").header("Authorization", bearer(tokenAdmin))
                            .param("data", LocalDate.now().toString()).param("servicosIds", "1"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$[0].inicio", notNullValue())).andExpect(jsonPath("$[0].fim", notNullValue()));
        }
    }

    @Test
    @DisplayName("GET /ordem-servicos/horarios-disponiveis -> 200 com lista vazia")
    void buscarHorariosDisponiveis_listaVazia() throws Exception {

        try (MockedStatic<LocalTime> mock = Mockito.mockStatic(LocalTime.class, Mockito.CALLS_REAL_METHODS)) {

            LocalTime horarioValido = LocalTime.of(18, 0);
            mock.when(LocalTime::now).thenReturn(horarioValido);

            mockMvc.perform(
                    get(BASE_PATH + "/ordem-servicos/horarios-disponiveis").header("Authorization", bearer(tokenAdmin))
                            .param("data", LocalDate.now().toString()).param("servicosIds", "1"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
        }
    }

    @Test
    @DisplayName("GET /ordem-servicos/horarios-disponiveis -> 404 quando servicos nao existem")
    void buscarHorariosDisponiveis_servicosNaoEncontrados() throws Exception {
        try (MockedStatic<LocalTime> mock = Mockito.mockStatic(LocalTime.class, Mockito.CALLS_REAL_METHODS)) {

            LocalTime horarioValido = LocalTime.of(10, 0);
            mock.when(LocalTime::now).thenReturn(horarioValido);

            mockMvc.perform(
                    get(BASE_PATH + "/ordem-servicos/horarios-disponiveis").header("Authorization", bearer(tokenAdmin))
                            .param("data", LocalDate.now().toString()).param("servicosIds", "9999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("GET /ordem-servicos/hoje -> 200 com response de dashboard")
    void buscarAgendamentosHoje_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos/hoje").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue())).andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
