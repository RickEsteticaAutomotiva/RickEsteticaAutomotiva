package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.domain.entity.assistente.ChamadaFuncao;
import com.automotiva.estetica.rick.domain.entity.assistente.MensagemAssistente;
import com.automotiva.estetica.rick.domain.entity.assistente.PapelMensagem;
import com.automotiva.estetica.rick.domain.entity.assistente.ParteMensagem;
import com.automotiva.estetica.rick.domain.entity.assistente.RespostaAssistente;
import com.automotiva.estetica.rick.domain.exception.IntegracaoException;
import com.automotiva.estetica.rick.domain.gateway.AiAssistantGateway;
import com.automotiva.estetica.rick.infrastructure.config.GeminiProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Implementação do {@link AiAssistantGateway} para a Gemini Generative
 * Language API (Google AI Studio), usando {@link RestClient} — sem SDK extra,
 * já disponível via spring-boot-starter-web. Trocar de provedor de IA no
 * futuro significa escrever outra implementação desta interface; nada em
 * domain/usecase ou application muda.
 */
@Slf4j
@Component
public class GeminiAssistantGateway implements AiAssistantGateway {

    private static final ZoneId ZONE_ID_SAO_PAULO = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter FORMATADOR_DIA_SEMANA = DateTimeFormatter.ofPattern("EEEE",
            new Locale("pt", "BR"));

    private final RestClient restClient;
    private final GeminiProperties properties;
    private final ObjectMapper objectMapper;
    private final String systemInstruction;

    public GeminiAssistantGateway(RestClient.Builder restClientBuilder, GeminiProperties properties,
            ObjectMapper objectMapper,
            @Value("classpath:prompts/assistente-system-instruction.txt") Resource systemInstructionResource) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder.baseUrl(properties.getBaseUrl()).build();
        this.systemInstruction = lerSystemInstruction(systemInstructionResource);
    }

    private String lerSystemInstruction(Resource resource) {
        try (var input = resource.getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível carregar o prompt do assistente", e);
        }
    }

    /**
     * O treinamento do modelo tem uma data de corte no passado, então sem essa
     * informação o Gemini assume um ano desatualizado ao calcular "hoje",
     * "amanhã" etc. Como o ano muda com o tempo, isso é calculado a cada
     * chamada (não no construtor, junto do prompt estático).
     */
    private String contextoDataAtual() {
        LocalDate hoje = LocalDate.now(ZONE_ID_SAO_PAULO);
        String diaSemana = FORMATADOR_DIA_SEMANA.format(hoje);
        return "Contexto: hoje é " + diaSemana + ", " + hoje + " (horário de Brasília, formato YYYY-MM-DD). "
                + "Use sempre esta data como referência real para calcular \"hoje\", \"amanhã\", \"essa semana\" "
                + "e datas relativas — nunca assuma outro ano ou data por conta própria.";
    }

    @Override
    public RespostaAssistente enviarMensagem(List<MensagemAssistente> historico) {
        ObjectNode corpo = objectMapper.createObjectNode();
        corpo.set("contents", montarConteudos(historico));
        corpo.set("tools", CatalogoFerramentasAssistente.declaracoes(objectMapper));

        ObjectNode instrucaoSistema = corpo.putObject("systemInstruction");
        instrucaoSistema.putArray("parts").addObject().put("text", systemInstruction + "\n\n" + contextoDataAtual());

        try {
            JsonNode resposta = restClient.post().uri("/models/{model}:generateContent", properties.getModel())
                    .header("x-goog-api-key", properties.getApiKey()).contentType(MediaType.APPLICATION_JSON)
                    .body(corpo).retrieve().onStatus(HttpStatusCode::isError, (req, res) -> {
                        String detalhe = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        log.warn("Gemini retornou erro {}: {}", res.getStatusCode(), detalhe);
                        throw IntegracaoException.builder()
                                .mensagem("Não consegui consultar o assistente agora. Tente novamente em instantes.")
                                .detalhes(detalhe).build();
                    }).body(JsonNode.class);
            return interpretarResposta(resposta);
        } catch (IntegracaoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Falha ao chamar a API do Gemini", e);
            throw IntegracaoException.builder()
                    .mensagem("Não consegui consultar o assistente agora. Tente novamente em instantes.")
                    .detalhes(e.getMessage()).build();
        }
    }

    private ArrayNode montarConteudos(List<MensagemAssistente> historico) {
        ArrayNode conteudos = objectMapper.createArrayNode();
        for (MensagemAssistente mensagem : historico) {
            ObjectNode conteudo = conteudos.addObject();
            conteudo.put("role", mensagem.getPapel() == PapelMensagem.USUARIO ? "user" : "model");
            ArrayNode partes = conteudo.putArray("parts");
            for (ParteMensagem parte : mensagem.getPartes()) {
                montarParte(partes.addObject(), parte);
            }
        }
        return conteudos;
    }

    private void montarParte(ObjectNode parteJson, ParteMensagem parte) {
        if (parte.getTexto() != null) {
            parteJson.put("text", parte.getTexto());
        } else if (parte.getChamadaFuncao() != null) {
            ObjectNode chamada = parteJson.putObject("functionCall");
            chamada.put("name", parte.getChamadaFuncao().getNome());
            chamada.set("args", objectMapper.valueToTree(valorOuVazio(parte.getChamadaFuncao().getArgumentos())));
        } else if (parte.getRespostaFuncao() != null) {
            ObjectNode resposta = parteJson.putObject("functionResponse");
            resposta.put("name", parte.getRespostaFuncao().getNome());
            resposta.set("response", objectMapper.valueToTree(valorOuVazio(parte.getRespostaFuncao().getResposta())));
        }
    }

    private Map<String, Object> valorOuVazio(Map<String, Object> valor) {
        return valor != null ? valor : Map.of();
    }

    private RespostaAssistente interpretarResposta(JsonNode respostaJson) {
        JsonNode candidatos = respostaJson.path("candidates");
        if (!candidatos.isArray() || candidatos.isEmpty()) {
            throw IntegracaoException.builder().mensagem("O assistente não retornou uma resposta válida. Tente novamente.")
                    .detalhes(respostaJson.toString()).build();
        }

        JsonNode partes = candidatos.get(0).path("content").path("parts");
        StringBuilder texto = new StringBuilder();
        List<ChamadaFuncao> chamadas = new ArrayList<>();

        for (JsonNode parte : partes) {
            if (parte.hasNonNull("text")) {
                if (!texto.isEmpty()) {
                    texto.append('\n');
                }
                texto.append(parte.get("text").asText());
            } else if (parte.has("functionCall")) {
                JsonNode chamadaJson = parte.get("functionCall");
                Map<String, Object> argumentos = objectMapper.convertValue(chamadaJson.path("args"),
                        new TypeReference<Map<String, Object>>() {
                        });
                chamadas.add(ChamadaFuncao.builder().nome(chamadaJson.path("name").asText()).argumentos(argumentos).build());
            }
        }

        return RespostaAssistente.builder().texto(!texto.isEmpty() ? texto.toString() : null).chamadasFuncao(chamadas)
                .build();
    }
}
