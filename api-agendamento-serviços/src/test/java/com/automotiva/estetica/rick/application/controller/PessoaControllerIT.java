package com.automotiva.estetica.rick.application.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.automotiva.estetica.rick.application.dto.request.PessoaAtualizacaoRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaCadastroRequest;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("IT — PessoaController")
class PessoaControllerIT extends AbstractIntegrationTest {

    // ─── Cadastro ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /pessoas/ → 201 ao cadastrar nova pessoa")
    void cadastrar_sucesso() throws Exception {
        PessoaCadastroRequest req = PessoaCadastroRequest.builder().nome("Carlos Novo").cpf("99988877766")
                .email("carlos.novo@email.com").telefone("11911223344").dataNascimento(LocalDate.of(1995, 3, 10))
                .senha("senha123").build();

        mockMvc.perform(post(BASE_PATH + "/pessoas/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("carlos.novo@email.com")))
                .andExpect(jsonPath("$.nome", is("Carlos Novo")));
    }

    @Test
    @DisplayName("POST /pessoas/ → 409 quando CPF já existe")
    void cadastrar_cpfDuplicado() throws Exception {
        PessoaCadastroRequest req = PessoaCadastroRequest.builder().nome("Duplicado CPF").cpf("12345678901") // CPF do
                                                                                                             // admin já
                                                                                                             // inserido
                                                                                                             // no seed
                .email("outroemail@email.com").senha("senha123").build();

        mockMvc.perform(post(BASE_PATH + "/pessoas/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /pessoas/ → 409 quando e-mail já existe")
    void cadastrar_emailDuplicado() throws Exception {
        PessoaCadastroRequest req = PessoaCadastroRequest.builder().nome("Duplicado Email").cpf("00011122233")
                .email("rodrigoapolodev@gmail.com") // e-mail do admin já no seed
                .senha("senha123").build();

        mockMvc.perform(post(BASE_PATH + "/pessoas/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /pessoas/ → 400 quando campos obrigatórios ausentes")
    void cadastrar_validacaoFalha() throws Exception {
        // nome e email são obrigatórios
        String json = "{\"cpf\":\"11122233344\",\"senha\":\"abc\"}";

        mockMvc.perform(post(BASE_PATH + "/pessoas/").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    // ─── Login ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /pessoas/login → 200 com token JWT")
    void login_sucesso() throws Exception {
        String body = "{\"email\":\"rodrigoapolodev@gmail.com\",\"senha\":\"rick@2024\"}";

        mockMvc.perform(post(BASE_PATH + "/pessoas/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token", not(emptyString())))
                .andExpect(jsonPath("$.email", is("rodrigoapolodev@gmail.com")));
    }

    @Test
    @DisplayName("POST /pessoas/login → 401 com credencial incorreta")
    void login_credencialErrada() throws Exception {
        String body = "{\"email\":\"rodrigoapolodev@gmail.com\",\"senha\":\"errada\"}";

        mockMvc.perform(post(BASE_PATH + "/pessoas/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }

    // ─── Busca ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /pessoas → 200 lista paginada autenticado")
    void buscarTodos_autenticado() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/pessoas").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /pessoas → 401 sem token")
    void buscarTodos_semToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/pessoas")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /pessoas/{id} → 200 ao buscar admin por ID")
    void buscarPorId_sucesso() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/pessoas/1").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email", is("rodrigoapolodev@gmail.com")));
    }

    @Test
    @DisplayName("GET /pessoas/{id} → 403 quando usuário tenta acessar recurso de outro usuário")
    void buscarPorId_naoEncontrado() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/pessoas/9999").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isForbidden());
    }

    // ─── Atualização ────────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /pessoas/{id} → 200 ao atualizar pessoa")
    void atualizar_sucesso() throws Exception {
        PessoaAtualizacaoRequest req = PessoaAtualizacaoRequest.builder().nome("Rodrigo Atualizado").build();

        mockMvc.perform(put(BASE_PATH + "/pessoas/1").header("Authorization", bearer(tokenAdmin))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome", is("Rodrigo Atualizado")));
    }

    @Test
    @DisplayName("PUT /pessoas/{id} → 403 quando usuário tenta atualizar recurso de outro usuário")
    void atualizar_naoEncontrado() throws Exception {
        PessoaAtualizacaoRequest req = PessoaAtualizacaoRequest.builder().nome("Teste").build();

        mockMvc.perform(put(BASE_PATH + "/pessoas/9999").header("Authorization", bearer(tokenAdmin))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ─── Deleção ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /pessoas/{id} → 403 quando usuário tenta remover recurso de outro usuário")
    @Sql(scripts = "/seed-extra-pessoa.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deletar_sucesso() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/pessoas/100").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /pessoas/{id} → 403 quando usuário tenta remover recurso de outro usuário")
    void deletar_naoEncontrado() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/pessoas/9999").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isForbidden());
    }
}
