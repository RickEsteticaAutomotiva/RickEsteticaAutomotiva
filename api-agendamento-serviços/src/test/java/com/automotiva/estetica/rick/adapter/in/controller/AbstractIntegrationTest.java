package com.automotiva.estetica.rick.adapter.in.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.automotiva.estetica.rick.ApiAgendamentoApplication;
import com.automotiva.estetica.rick.application.port.out.CalendarioPort;
import com.automotiva.estetica.rick.application.port.out.EmailPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Classe base para testes de integração.
 *
 * <p>Sobe o contexto Spring completo com H2 em memória, mocka os adaptadores externos
 * (CalendarioPort e EmailPort) e disponibiliza utilitários como MockMvc, ObjectMapper e
 * helper para autenticação JWT.
 *
 * <p>A anotação @Sql garante que o banco seja resetado (drop + create + seed) antes de cada
 * classe de teste, mantendo o isolamento entre as suítes.
 *
 * <p>As senhas dos usuários de teste são codificadas dinamicamente pelo PasswordEncoder real
 * do contexto Spring, eliminando dependência de hashes BCrypt hardcoded no seed SQL.
 */
@SpringBootTest(classes = ApiAgendamentoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Sql(
        scripts = {"/reset-it.sql", "/seed-it.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public abstract class AbstractIntegrationTest {

    /**
     * BASE_PATH vazio: com server.servlet.context-path=/ no profile integration-test,
     * o MockMvc não precisa de nenhum prefixo. As URLs nos testes são diretas: "/pessoas/login".
     */
    protected static final String BASE_PATH = "";

    // Credenciais dos usuários criados pelo seed-it.sql
    protected static final String EMAIL_ADMIN = "rodrigoapolodev@gmail.com";
    protected static final String SENHA_ADMIN = "rick@2024";
    protected static final String EMAIL_USER = "maria.santos@email.com";
    protected static final String SENHA_USER = "senha123";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Mock do Google Calendar — impede conexão OAuth real durante os testes */
    @MockitoBean
    @SuppressWarnings("unused")
    protected CalendarioPort calendarioPort;

    /** Mock do e-mail — impede envio real de mensagens durante os testes */
    @MockitoBean
    @SuppressWarnings("unused")
    protected EmailPort emailPort;

    protected String tokenAdmin;
    protected String tokenUser;

    /**
     * Garante que as senhas no banco H2 correspondem às credenciais de teste,
     * codificando-as com o PasswordEncoder real do contexto Spring.
     * Depois obtém os tokens JWT reais via POST /pessoas/login.
     */
    @BeforeEach
    void autenticar() throws Exception {
        atualizarSenhaNoBank(EMAIL_ADMIN, SENHA_ADMIN);
        atualizarSenhaNoBank(EMAIL_USER, SENHA_USER);

        tokenAdmin = obterToken(EMAIL_ADMIN, SENHA_ADMIN);
        tokenUser = obterToken(EMAIL_USER, SENHA_USER);
    }

    /**
     * Atualiza a senha do usuário diretamente no H2 usando o BCrypt encoder real,
     * garantindo que o hash no banco sempre bate com a credencial usada nos testes.
     */
    private void atualizarSenhaNoBank(String email, String senha) {
        String hash = passwordEncoder.encode(senha);
        new JdbcTemplate(dataSource).update("UPDATE pessoa SET senha = ? WHERE email = ?", hash, email);
    }

    private String obterToken(String email, String senha) throws Exception {
        String body = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);

        MvcResult result = mockMvc.perform(post(BASE_PATH + "/pessoas/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        // Extrai o token do JSON { "token": "..." }
        return objectMapper.readTree(response).get("token").asText();
    }

    /** Monta o header Authorization Bearer para uso nas requisições autenticadas. */
    protected String bearer(String token) {
        return "Bearer " + token;
    }
}
