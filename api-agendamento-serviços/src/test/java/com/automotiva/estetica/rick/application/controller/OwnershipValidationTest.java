package com.automotiva.estetica.rick.application.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.automotiva.estetica.rick.application.dto.request.PessoaAtualizacaoRequest;
import com.automotiva.estetica.rick.application.dto.request.SenhaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/**
 * Testes de integração para validação de Ownership — OWASP A01 (Broken Access
 * Control).
 *
 * <p>
 * Valida que usuários não conseguem acessar/modificar dados uns dos outros.
 *
 * @see <a href="https://owasp.org/Top10/A01_2021-Broken_Access_Control/">OWASP
 *      A01</a>
 */
@DisplayName("IT — OwnershipValidation (OWASP A01)")
class OwnershipValidationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    // ───────────────────────────────────────────────────────────────────────
    // GET /pessoas/{id} — buscarPorId deve bloquear acesso a outro usuário
    // ───────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /pessoas/{id} → 403 ao tentar buscar dados de outro usuário")
    void buscarPorId_acessoAOutroUsuario_deve403() throws Exception {
        // tokenUser é da pessoa com email maria.santos@email.com (ID=2 no seed)
        // Tenta acessar /pessoas/1 (rodrigoapolodev@gmail.com)
        mockMvc.perform(get(BASE_PATH + "/pessoas/1").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type", equalTo("https://api.rickestetica.com.br/errors/acesso_negado")))
                .andExpect(jsonPath("$.title", equalTo("ACESSO_NEGADO")));
    }

    @Test
    @DisplayName("GET /pessoas/{id} → 200 ao buscar dados próprios")
    void buscarPorId_recursoProprio_deve200() throws Exception {
        // tokenUser tenta acessar seu próprio perfil (ID=2)
        mockMvc.perform(get(BASE_PATH + "/pessoas/2").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email", equalTo(EMAIL_USER)));
    }

    // ───────────────────────────────────────────────────────────────────────
    // PUT /pessoas/{id} — atualizar deve bloquear acesso a outro usuário
    // ───────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /pessoas/{id} → 403 ao tentar atualizar outro usuário")
    void atualizar_acessoAOutroUsuario_deve403() throws Exception {
        // tokenUser tenta atualizar dados de pessoa 1 (admin)
        PessoaAtualizacaoRequest req = new PessoaAtualizacaoRequest();
        req.setNome("Pessoa Alterada");

        mockMvc.perform(put(BASE_PATH + "/pessoas/1").header("Authorization", bearer(tokenUser))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.title", equalTo("ACESSO_NEGADO")));
    }

    @Test
    @DisplayName("PUT /pessoas/{id} → 200 ao atualizar dados próprios")
    void atualizar_recursoProprio_deve200() throws Exception {
        // tokenUser atualiza seu próprio perfil (ID=2)
        PessoaAtualizacaoRequest req = new PessoaAtualizacaoRequest();
        req.setNome("Maria Atualizada");

        mockMvc.perform(put(BASE_PATH + "/pessoas/2").header("Authorization", bearer(tokenUser))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome", equalTo("Maria Atualizada")));
    }

    // ───────────────────────────────────────────────────────────────────────
    // DELETE /pessoas/{id} — deletar deve bloquear acesso a outro usuário
    // ───────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /pessoas/{id} → 403 ao tentar deletar outro usuário")
    void deletar_acessoAOutroUsuario_deve403() throws Exception {
        // tokenUser tenta deletar pessoa 1 (admin)
        mockMvc.perform(delete(BASE_PATH + "/pessoas/1").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.title", equalTo("ACESSO_NEGADO")));
    }

    @Test
    @DisplayName("DELETE /pessoas/{id} → 204 ao deletar próprio perfil")
    void deletar_recursoProprio_deve204() throws Exception {
        // tokenUser deleta seu próprio perfil (ID=2)
        mockMvc.perform(delete(BASE_PATH + "/pessoas/2").header("Authorization", bearer(tokenUser)))
                .andExpect(status().isNoContent());
    }

    // ───────────────────────────────────────────────────────────────────────
    // PATCH /pessoas/{id}/senha — atualizar senha deve bloquear outro usuário
    // ───────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("PATCH /pessoas/{id}/senha → 403 ao tentar alterar senha de outro usuário")
    void atualizarSenha_acessoAOutroUsuario_deve403() throws Exception {
        // tokenUser tenta alterar senha de pessoa 1 (admin)
        SenhaRequest req = new SenhaRequest();
        req.setSenhaAtual("rick@2024");
        req.setNovaSenha("novaSenha123");

        mockMvc.perform(patch(BASE_PATH + "/pessoas/1/senha").header("Authorization", bearer(tokenUser))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.title", equalTo("ACESSO_NEGADO")));
    }

    @Test
    @DisplayName("PATCH /pessoas/{id}/senha → 204 ao alterar própria senha")
    void atualizarSenha_recursoProprio_deve204() throws Exception {
        // tokenUser altera sua própria senha (ID=2)
        SenhaRequest req = new SenhaRequest();
        req.setSenhaAtual(SENHA_USER);
        req.setNovaSenha("novaSenha@2024");

        mockMvc.perform(patch(BASE_PATH + "/pessoas/2/senha").header("Authorization", bearer(tokenUser))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    // ───────────────────────────────────────────────────────────────────────
    // Testes sem autenticação — devem retornar 401
    // ───────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /pessoas/{id} → 401 sem token JWT")
    void buscarPorId_semToken_deve401() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/pessoas/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /pessoas/{id} → 401 sem token JWT")
    void atualizar_semToken_deve401() throws Exception {
        PessoaAtualizacaoRequest req = new PessoaAtualizacaoRequest();
        req.setNome("Teste");

        mockMvc.perform(put(BASE_PATH + "/pessoas/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /pessoas/{id} → 401 sem token JWT")
    void deletar_semToken_deve401() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/pessoas/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /pessoas/{id}/senha → 401 sem token JWT")
    void atualizarSenha_semToken_deve401() throws Exception {
        SenhaRequest req = new SenhaRequest();
        req.setSenhaAtual("teste");
        req.setNovaSenha("teste2");

        mockMvc.perform(patch(BASE_PATH + "/pessoas/1/senha").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isUnauthorized());
    }
}
