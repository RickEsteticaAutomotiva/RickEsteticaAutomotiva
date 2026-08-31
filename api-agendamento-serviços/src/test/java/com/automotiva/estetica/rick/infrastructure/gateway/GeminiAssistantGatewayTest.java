package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.automotiva.estetica.rick.domain.entity.assistente.MensagemAssistente;
import com.automotiva.estetica.rick.domain.entity.assistente.PapelMensagem;
import com.automotiva.estetica.rick.domain.entity.assistente.ParteMensagem;
import com.automotiva.estetica.rick.domain.entity.assistente.RespostaAssistente;
import com.automotiva.estetica.rick.domain.exception.IntegracaoException;
import com.automotiva.estetica.rick.infrastructure.config.GeminiProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class GeminiAssistantGatewayTest {

    private static final String URI_GERACAO = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private MockRestServiceServer server;
    private GeminiAssistantGateway gateway;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();

        GeminiProperties properties = new GeminiProperties();
        properties.setApiKey("chave-de-teste");
        properties.setModel("gemini-2.5-flash");
        properties.setBaseUrl("https://generativelanguage.googleapis.com/v1beta");

        gateway = new GeminiAssistantGateway(builder, properties, new ObjectMapper(),
                new ClassPathResource("prompts/assistente-system-instruction.txt"));
    }

    private List<MensagemAssistente> historicoSimples() {
        return List.of(MensagemAssistente.builder().papel(PapelMensagem.USUARIO)
                .partes(List.of(ParteMensagem.builder().texto("Oi").build())).build());
    }

    @Test
    void enviarMensagem_deveRetornarTexto_quandoRespostaTemApenasTexto() {
        server.expect(requestTo(URI_GERACAO)).andExpect(method(HttpMethod.POST))
                .andExpect(header("x-goog-api-key", "chave-de-teste"))
                .andRespond(withSuccess("""
                        {
                          "candidates": [
                            { "content": { "parts": [ { "text": "Olá! Como posso ajudar?" } ] } }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        RespostaAssistente resposta = gateway.enviarMensagem(historicoSimples());

        assertEquals("Olá! Como posso ajudar?", resposta.getTexto());
        assertTrue(resposta.getChamadasFuncao().isEmpty());
        server.verify();
    }

    @Test
    void enviarMensagem_deveRetornarChamadaFuncao_quandoModeloPropoeTool() {
        server.expect(requestTo(URI_GERACAO)).andRespond(withSuccess("""
                {
                  "candidates": [
                    { "content": { "parts": [
                        { "functionCall": { "name": "buscarServicos", "args": { "termo": "polimento de farol" } } }
                    ] } }
                  ]
                }
                """, MediaType.APPLICATION_JSON));

        RespostaAssistente resposta = gateway.enviarMensagem(historicoSimples());

        assertEquals(1, resposta.getChamadasFuncao().size());
        assertEquals("buscarServicos", resposta.getChamadasFuncao().get(0).getNome());
        assertEquals("polimento de farol", resposta.getChamadasFuncao().get(0).getArgumentos().get("termo"));
        server.verify();
    }

    @Test
    void enviarMensagem_deveLancarIntegracaoException_quandoGeminiRetornaErro() {
        server.expect(requestTo(URI_GERACAO))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).body("{\"error\":\"invalid\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThrows(IntegracaoException.class, () -> gateway.enviarMensagem(historicoSimples()));
        server.verify();
    }
}
