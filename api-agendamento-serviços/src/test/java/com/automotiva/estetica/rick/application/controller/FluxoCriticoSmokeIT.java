package com.automotiva.estetica.rick.application.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

@DisplayName("IT - FluxoCriticoSmoke")
class FluxoCriticoSmokeIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("E2E smoke - login, agendamento, gestao de status e consulta final")
    void fluxoCritico_loginAgendamentoStatusGestao_sucesso() throws Exception {
        String tokenCliente = login(EMAIL_USER, SENHA_USER);
        String tokenGestao = login(EMAIL_GERENTE, SENHA_GERENTE);

        LocalDateTime dataAgendamento = LocalDateTime.now().plusDays(10).withSecond(0).withNano(0);
        String payloadCriacao = String.format(
                "{\"dataAgendamento\":\"%s\",\"veiculo\":2,\"precoMinimo\":150.00,\"servicos\":[1,2],\"observacoes\":\"Smoke H1.3\"}",
                dataAgendamento);

        MvcResult criacao = mockMvc
                .perform(post(BASE_PATH + "/ordem-servicos").header("Authorization", bearer(tokenCliente))
                        .contentType(MediaType.APPLICATION_JSON).content(payloadCriacao))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.id", notNullValue())).andReturn();

        JsonNode ordemCriada = objectMapper.readTree(criacao.getResponse().getContentAsString());
        long idOrdem = ordemCriada.get("id").asLong();

        mockMvc.perform(
                patch(BASE_PATH + "/ordem-servicos-gestao/" + idOrdem).header("Authorization", bearer(tokenGestao))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":2}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status.id").value(2));

        mockMvc.perform(
                patch(BASE_PATH + "/ordem-servicos-gestao/" + idOrdem).header("Authorization", bearer(tokenGestao))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":5}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status.id").value(5));

        mockMvc.perform(get(BASE_PATH + "/ordem-servicos/" + idOrdem).header("Authorization", bearer(tokenCliente)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(idOrdem))
                .andExpect(jsonPath("$.status.id").value(5)).andExpect(jsonPath("$.dtConclusao", notNullValue()));
    }

    @Test
    @DisplayName("E2E smoke - cliente nao pode atualizar status de gestao")
    void fluxoCritico_clienteNaoPodeAtualizarStatusGestao() throws Exception {
        String tokenCliente = login(EMAIL_USER, SENHA_USER);

        LocalDateTime dataAgendamento = LocalDateTime.now().plusDays(5).withSecond(0).withNano(0);
        String payloadCriacao = String.format(
                "{\"dataAgendamento\":\"%s\",\"veiculo\":2,\"precoMinimo\":100.00,\"servicos\":[1],\"observacoes\":\"Smoke auth\"}",
                dataAgendamento);

        MvcResult criacao = mockMvc
                .perform(post(BASE_PATH + "/ordem-servicos").header("Authorization", bearer(tokenCliente))
                        .contentType(MediaType.APPLICATION_JSON).content(payloadCriacao))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.id", notNullValue())).andReturn();

        long idOrdem = objectMapper.readTree(criacao.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(
                patch(BASE_PATH + "/ordem-servicos-gestao/" + idOrdem).header("Authorization", bearer(tokenCliente))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":2}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("E2E smoke - endpoint de gestao requer token")
    void fluxoCritico_gestaoSemToken_deveRetornar401() throws Exception {
        mockMvc.perform(patch(BASE_PATH + "/ordem-servicos-gestao/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":2}")).andExpect(status().isUnauthorized());
    }

    private String login(String email, String senha) throws Exception {
        String body = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);

        MvcResult result = mockMvc
                .perform(post(BASE_PATH + "/pessoas/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}
