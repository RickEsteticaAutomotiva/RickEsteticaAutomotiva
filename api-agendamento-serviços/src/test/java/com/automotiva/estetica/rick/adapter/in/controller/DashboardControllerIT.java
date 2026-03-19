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
    @DisplayName("GET /dashboard/faturamento-periodo → 200 retorna contrato com dia e totalDia")
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

    @Test
    @DisplayName("GET /dashboard/faturamento-servicos → 200 retorna faturamento agrupado por serviço")
    void buscarFaturamentoServicos_sucesso() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/dashboard/faturamento-servicos")
                                .header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /dashboard/faturamento-servicos → 403 para usuário sem ROLE_GERENTE")
    void buscarFaturamentoServicos_semPermissao() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/dashboard/faturamento-servicos")
                                .header("Authorization", bearer(tokenUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /dashboard/fluxo-caixa → 200 retorna objeto consolidado")
    void buscarFluxoCaixa_sucesso() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/dashboard/fluxo-caixa")
                                .header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").exists())
                .andExpect(jsonPath("$.lucro").exists())
                .andExpect(jsonPath("$.custo").exists())
                .andExpect(jsonPath("$.percentualLucro").exists())
                .andExpect(jsonPath("$.percentualCusto").exists());
    }

    @Test
    @DisplayName("GET /dashboard/fluxo-caixa → 403 para usuário sem ROLE_GERENTE")
    void buscarFluxoCaixa_semPermissao() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/dashboard/fluxo-caixa")
                                .header("Authorization", bearer(tokenUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /dashboard/cancelamentos → 200 retorna array de cancelamentos por tipo")
    void buscarCancelamentos_sucesso() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/dashboard/cancelamentos")
                                .header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /dashboard/cancelamentos → 403 para usuário sem ROLE_GERENTE")
    void buscarCancelamentos_semPermissao() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/dashboard/cancelamentos")
                                .header("Authorization", bearer(tokenUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /dashboard/home-resumo → 200 retorna objeto com chaves esperadas")
    void buscarHomeResumo_sucesso() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/dashboard/home-resumo")
                                .header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agendamentosHoje").exists())
                .andExpect(jsonPath("$.faturamentoEstimadoHoje").exists())
                .andExpect(jsonPath("$.ticketMedioEstimadoHoje").exists())
                .andExpect(jsonPath("$.proximoAgendamento").exists());
    }

    @Test
    @DisplayName("GET /dashboard/home-resumo → 401 sem token")
    void buscarHomeResumo_semToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/dashboard/home-resumo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /dashboard/home-resumo → 403 para usuário sem ROLE_GERENTE")
    void buscarHomeResumo_semPermissao() throws Exception {
        mockMvc.perform(
                        get(BASE_PATH + "/dashboard/home-resumo")
                                .header("Authorization", bearer(tokenUser)))
                .andExpect(status().isForbidden());
    }
}
