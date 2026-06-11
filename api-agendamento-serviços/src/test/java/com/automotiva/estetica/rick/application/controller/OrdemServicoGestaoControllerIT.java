package com.automotiva.estetica.rick.application.controller;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("IT — OrdemServicoGestaoController")
class OrdemServicoGestaoControllerIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("GET /ordem-servicos-gestao → 200 para gerente")
    void buscarTodosParaGestao_comGerente() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos-gestao").header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /ordem-servicos-gestao → 403 para cliente")
    void buscarTodosParaGestao_comClienteDeveNegar() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos-gestao").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /ordem-servicos-gestao/hoje → 200 para gerente")
    void buscarAgendamentosHoje_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos-gestao/hoje").header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue())).andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("GET /ordem-servicos-gestao/{id} → 200 e shape esperado")
    void buscarDetalheParaGestao_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/ordem-servicos-gestao/1").header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status.id", notNullValue())).andExpect(jsonPath("$.cliente.id", notNullValue()))
                .andExpect(jsonPath("$.veiculo.id", notNullValue()))
                .andExpect(jsonPath("$.servicos", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id} → 200 ao atualizar status")
    void atualizarStatusParaGestao_sucesso() throws Exception {
        String body = "{\"status\":2}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1").header("Authorization", bearer(tokenGerente))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status.id", notNullValue()));
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id} → 200 ao atualizar data, observações e status")
    void atualizarParaGestao_comTodosCampos_sucesso() throws Exception {
        String body = "{\"dataAgendamento\":\"2026-05-15T10:30:00\",\"observacoes\":\"Cliente pediu prioridade\",\"status\":2}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1").header("Authorization", bearer(tokenGerente))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue())).andExpect(jsonPath("$.status.id", notNullValue()))
                .andExpect(jsonPath("$.dataAgendamento", notNullValue()))
                .andExpect(jsonPath("$.observacoes", notNullValue()));
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id} → 200 ao atualizar apenas status")
    void atualizarParaGestao_apenasStatus_sucesso() throws Exception {
        String body = "{\"status\":3}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1").header("Authorization", bearer(tokenGerente))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status.id", notNullValue()));
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id} → 200 ao atualizar data e status")
    void atualizarParaGestao_dataEStatus_sucesso() throws Exception {
        String body = "{\"dataAgendamento\":\"2026-06-20T14:00:00\",\"status\":1}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1").header("Authorization", bearer(tokenGerente))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk())
                .andExpect(jsonPath("$.dataAgendamento", notNullValue()));
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id} → 200 ao atualizar observações e status")
    void atualizarParaGestao_observacoesEStatus_sucesso() throws Exception {
        String body = "{\"observacoes\":\"Novo cliente, primeira vez\",\"status\":1}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1").header("Authorization", bearer(tokenGerente))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk())
                .andExpect(jsonPath("$.observacoes", notNullValue()));
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id} → 400 quando status é nulo")
    void atualizarParaGestao_statusNulo_erro() throws Exception {
        String body = "{\"dataAgendamento\":\"2026-04-22T14:30:00\"}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1").header("Authorization", bearer(tokenGerente))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id} → 400 quando status é inválido")
    void atualizarParaGestao_statusInvalido_erro() throws Exception {
        String body = "{\"dataAgendamento\":\"2026-04-22T14:30:00\",\"status\":99}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1").header("Authorization", bearer(tokenGerente))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id} → 400 quando observações excedem limite")
    void atualizarParaGestao_observacoesExcedeLimite_erro() throws Exception {
        String observacoesLongas = "x".repeat(2001);
        String body = "{\"observacoes\":\"" + observacoesLongas + "\",\"status\":1}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1").header("Authorization", bearer(tokenGerente))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id} → 403 para cliente")
    void atualizarStatusParaGestao_comClienteDeveNegar() throws Exception {
        String body = "{\"status\":2}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1").header("Authorization", bearer(tokenUser))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /ordem-servicos-gestao/{id}/servicos → 201 ao adicionar serviço")
    void adicionarServicosParaGestao_sucesso() throws Exception {
        String body = "{\"servicos\":[{\"idServico\":2,\"valorAplicado\":45.00}]}";

        mockMvc.perform(post(BASE_PATH + "/ordem-servicos-gestao/1/servicos")
                .header("Authorization", bearer(tokenGerente)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id}/servicos/{servicoId} → 200 ao atualizar valor")
    void atualizarValorServicoParaGestao_sucesso() throws Exception {
        String body = "{\"valorAplicado\":90.00}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1/servicos/1")
                .header("Authorization", bearer(tokenGerente)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("DELETE /ordem-servicos-gestao/{id}/servicos/{servicoId} → 200 ao remover serviço")
    void removerServicoParaGestao_sucesso() throws Exception {
        mockMvc.perform(
                delete(BASE_PATH + "/ordem-servicos-gestao/1/servicos/3").header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("PATCH gestão sem token → 401")
    void atualizarStatusParaGestao_semToken() throws Exception {
        String body = "{\"status\":2}";

        mockMvc.perform(
                patch(BASE_PATH + "/ordem-servicos-gestao/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id}/cancel → 200 ao cancelar com motivo")
    void cancelarParaGestao_sucesso() throws Exception {
        String body = "{\"motivo\":2}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1/cancel")
                .header("Authorization", bearer(tokenGerente)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status.id", notNullValue()))
                .andExpect(jsonPath("$.motivoCancelamento", notNullValue()));
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id}/cancel → 400 quando motivo ausente")
    void cancelarParaGestao_semMotivo_erro() throws Exception {
        String body = "{}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1/cancel")
                .header("Authorization", bearer(tokenGerente)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id}/cancel → 403 para cliente")
    void cancelarParaGestao_comClienteDeveNegar() throws Exception {
        String body = "{\"motivo\":1}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1/cancel").header("Authorization", bearer(tokenUser))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /ordem-servicos-gestao/{id}/cancel → 401 sem token")
    void cancelarParaGestao_semToken() throws Exception {
        String body = "{\"motivo\":1}";

        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1/cancel").contentType(MediaType.APPLICATION_JSON)
                .content(body)).andExpect(status().isUnauthorized());
    }
}
