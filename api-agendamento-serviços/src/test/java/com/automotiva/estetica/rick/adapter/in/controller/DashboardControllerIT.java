package com.automotiva.estetica.rick.adapter.in.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("IT — DashboardController")
class DashboardControllerIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("GET /dashboard/faturamento → 200 retorna faturamento do mês autenticado")
    void buscarFaturamentoTotal_autenticado() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/dashboard/faturamento").header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.faturamentoAtual").exists());
    }

    @Test
    @DisplayName("GET /dashboard/faturamento → 401 sem token")
    void buscarFaturamentoTotal_semToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/dashboard/faturamento")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /dashboard/total-ordens → 200 retorna qtd de ordens do mês")
    void buscarQtdOrdens_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/dashboard/total-ordens").header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalOrdens").exists());
    }

    @Test
    @DisplayName("GET /dashboard/servicos-concluidos → 200 retorna ordens concluídas do mês")
    void buscarServicosConcluidosMes_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/dashboard/servicos-concluidos").header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalOrdensConcluidas").exists());
    }

    @Test
    @DisplayName("GET /dashboard/ticket-medio → 200 retorna ticket médio do mês")
    void buscarTicketMedio_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/dashboard/ticket-medio").header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalTicketMedioMesAtual").exists());
    }

    @Test
    @DisplayName("GET /dashboard/faturamento-periodo → 200 retorna faturamento dos últimos 30 dias")
    void buscarFaturamentoPeriodo_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/dashboard/faturamento-periodo").header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /dashboard/faturamento-periodo → 403 para usuário sem ROLE_GERENTE")
    void buscarFaturamentoPeriodo_semPermissao() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/dashboard/faturamento-periodo").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isForbidden());
    }
}
