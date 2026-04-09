package com.automotiva.estetica.rick.application.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@DisplayName("IT - ErroLogController")
class ErroLogControllerIT extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("GET /erros-log -> 401 sem token")
    void buscarTodos_semToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/erros-log")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /erros-log -> 403 para usuario sem ROLE_ADMIN")
    void buscarTodos_semPermissao() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/erros-log").header("Authorization", bearer(tokenGerente)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /erros-log -> 200 para admin")
    void buscarTodos_admin() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/erros-log").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    @DisplayName("GET /erros-log/{id} -> deve retornar campos sensiveis redatados")
    void buscarPorId_deveRedatarCamposSensiveis() throws Exception {
        jdbcTemplate.update("""
                INSERT INTO erro_log (
                    timestamp, tipo_excecao, mensagem, stack_trace, endpoint, metodo_http,
                    payload_requisicao, query_params, headers_requisicao,
                    usuario_email, status_http, ambiente, ip_cliente, user_agent
                ) VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, "java.lang.IllegalStateException", "erro de teste", "ip=192.168.1.100", "/teste", "GET",
                "{\"senha\":\"abc\"}", "?token=abc&pagina=1", "Authorization: Bearer abc", "usuario@dominio.com", 500,
                "integration-test", "192.168.1.100", "JUnit");

        Long id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM erro_log", Long.class);

        mockMvc.perform(get(BASE_PATH + "/erros-log/{id}", id).header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.usuarioEmail").value("u***@dominio.com"))
                .andExpect(jsonPath("$.ipCliente").value("192.168.***.***"))
                .andExpect(jsonPath("$.payloadRequisicao").value("{\"senha\": \"***REDACTED***\"}"))
                .andExpect(jsonPath("$.queryParams").value("?token=***REDACTED***&pagina=1"));
    }

    @Test
    @DisplayName("GET /erros-log/filtros -> 200 com pagina filtrada")
    void buscarComFiltros_sucesso() throws Exception {
        jdbcTemplate.update("""
                INSERT INTO erro_log (
                    timestamp, tipo_excecao, mensagem, stack_trace, endpoint, metodo_http,
                    payload_requisicao, query_params, headers_requisicao,
                    usuario_email, status_http, ambiente, ip_cliente, user_agent
                ) VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, "java.lang.IllegalArgumentException", "filtro de teste", "stack", "/filtros", "POST", "{}", "?x=1",
                "H", "admin@dominio.com", 400, "integration-test", "10.0.0.1", "JUnit");

        mockMvc.perform(get(BASE_PATH + "/erros-log/filtros").param("tipoExcecao", "IllegalArgumentException")
                .header("Authorization", bearer(tokenAdmin))).andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }
}
