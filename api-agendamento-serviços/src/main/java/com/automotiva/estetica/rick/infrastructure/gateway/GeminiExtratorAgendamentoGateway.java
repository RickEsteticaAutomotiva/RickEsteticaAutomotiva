package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.domain.entity.CampoExtraido;
import com.automotiva.estetica.rick.domain.entity.DadosAgendamentoExtraido;
import com.automotiva.estetica.rick.domain.exception.IntegracaoException;
import com.automotiva.estetica.rick.domain.gateway.ExtratorAgendamentoGateway;
import com.automotiva.estetica.rick.infrastructure.config.GeminiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Implementação do {@link ExtratorAgendamentoGateway} para a Gemini
 * Generative Language API, usando {@link RestClient} (mesma abordagem sem SDK
 * do {@link GeminiAssistantGateway}). Diferente do gateway do assistente de
 * chat, esta chamada é single-shot e multimodal (texto + imagem), usando o
 * modo JSON estruturado do Gemini (responseSchema) para garantir um formato
 * de resposta previsível — inclusive a possibilidade de {@code null} em
 * qualquer campo não identificável, que é a base técnica da regra "nunca
 * inventar dado".
 */
@Slf4j
@Component
public class GeminiExtratorAgendamentoGateway implements ExtratorAgendamentoGateway {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ISO_LOCAL_DATE;

    private final RestClient restClient;
    private final GeminiProperties properties;
    private final ObjectMapper objectMapper;
    private final String systemInstruction;

    public GeminiExtratorAgendamentoGateway(RestClient.Builder restClientBuilder, GeminiProperties properties,
            ObjectMapper objectMapper,
            @Value("classpath:prompts/extracao-agendamento-system-instruction.txt") Resource systemInstructionResource) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder.baseUrl(properties.getBaseUrl()).build();
        this.systemInstruction = lerSystemInstruction(systemInstructionResource);
    }

    private String lerSystemInstruction(Resource resource) {
        try (var input = resource.getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível carregar o prompt de extração de agendamento", e);
        }
    }

    @Override
    public DadosAgendamentoExtraido extrair(String imagemBase64, String mimeType, LocalDate dataReferencia) {
        ObjectNode corpo = objectMapper.createObjectNode();

        ObjectNode conteudo = corpo.putArray("contents").addObject();
        conteudo.put("role", "user");
        var partes = conteudo.putArray("parts");
        partes.addObject().put("text",
                "Contexto: a data de referência para resolver expressões relativas (\"hoje\", \"amanhã\", \"sexta\") é "
                        + FORMATADOR_DATA.format(dataReferencia) + " (formato YYYY-MM-DD). Nunca assuma outra data.");
        ObjectNode inlineData = partes.addObject().putObject("inlineData");
        inlineData.put("mimeType", mimeType);
        inlineData.put("data", imagemBase64);

        ObjectNode instrucaoSistema = corpo.putObject("systemInstruction");
        instrucaoSistema.putArray("parts").addObject().put("text", systemInstruction);

        ObjectNode generationConfig = corpo.putObject("generationConfig");
        generationConfig.put("responseMimeType", "application/json");
        generationConfig.set("responseSchema", montarResponseSchema());

        try {
            JsonNode resposta = restClient.post().uri("/models/{model}:generateContent", properties.getModel())
                    .header("x-goog-api-key", properties.getApiKey()).contentType(MediaType.APPLICATION_JSON)
                    .body(corpo).retrieve().onStatus(HttpStatusCode::isError, (req, res) -> {
                        String detalhe = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        log.warn("Gemini retornou erro {} ao extrair agendamento: {}", res.getStatusCode(), detalhe);
                        throw IntegracaoException.builder()
                                .mensagem("Não foi possível processar a imagem agora. Tente novamente.")
                                .detalhes(detalhe).build();
                    }).body(JsonNode.class);
            return interpretarResposta(resposta);
        } catch (IntegracaoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Falha ao chamar a API do Gemini para extração de agendamento", e);
            throw IntegracaoException.builder()
                    .mensagem("Não foi possível processar a imagem agora. Tente novamente.").detalhes(e.getMessage())
                    .build();
        }
    }

    private DadosAgendamentoExtraido interpretarResposta(JsonNode respostaJson) {
        JsonNode candidatos = respostaJson.path("candidates");
        if (!candidatos.isArray() || candidatos.isEmpty()) {
            throw IntegracaoException.builder()
                    .mensagem("Não foi possível processar a imagem agora. Tente novamente.")
                    .detalhes(respostaJson.toString()).build();
        }

        String textoJson = candidatos.get(0).path("content").path("parts").path(0).path("text").asText(null);
        if (textoJson == null || textoJson.isBlank()) {
            throw IntegracaoException.builder()
                    .mensagem("Não foi possível processar a imagem agora. Tente novamente.")
                    .detalhes(respostaJson.toString()).build();
        }

        JsonNode extraido;
        try {
            extraido = objectMapper.readTree(textoJson);
        } catch (IOException e) {
            throw IntegracaoException.builder()
                    .mensagem("Não foi possível interpretar o resultado da extração. Tente novamente.")
                    .detalhes(textoJson).build();
        }

        return DadosAgendamentoExtraido.builder().nomeCliente(campoTexto(extraido, "nomeCliente"))
                .telefoneCliente(campoTexto(extraido, "telefoneCliente")).placaVeiculo(campoTexto(extraido, "placaVeiculo"))
                .modeloVeiculo(campoTexto(extraido, "modeloVeiculo"))
                .descricaoServico(campoTexto(extraido, "descricaoServico")).data(campoData(extraido, "data"))
                .horario(campoHorario(extraido, "horario")).valor(campoValor(extraido, "valor"))
                .observacoesLivres(extraido.path("observacoesLivres").asText(null)).build();
    }

    private CampoExtraido<String> campoTexto(JsonNode raiz, String nomeCampo) {
        JsonNode campo = raiz.path(nomeCampo);
        String valor = campo.path("valor").isNull() || campo.path("valor").isMissingNode() ? null
                : campo.path("valor").asText(null);
        return CampoExtraido.<String>builder().valor(valor != null && !valor.isBlank() ? valor : null)
                .confianca(confianca(campo)).build();
    }

    private CampoExtraido<LocalDate> campoData(JsonNode raiz, String nomeCampo) {
        JsonNode campo = raiz.path(nomeCampo);
        String texto = campo.path("valor").asText(null);
        LocalDate valor = null;
        if (texto != null && !texto.isBlank()) {
            try {
                valor = LocalDate.parse(texto, FORMATADOR_DATA);
            } catch (Exception e) {
                log.warn("Data retornada pelo Gemini em formato inesperado: {}", texto);
            }
        }
        return CampoExtraido.<LocalDate>builder().valor(valor).confianca(valor != null ? confianca(campo) : null)
                .build();
    }

    private CampoExtraido<LocalTime> campoHorario(JsonNode raiz, String nomeCampo) {
        JsonNode campo = raiz.path(nomeCampo);
        String texto = campo.path("valor").asText(null);
        LocalTime valor = null;
        if (texto != null && !texto.isBlank()) {
            try {
                valor = LocalTime.parse(texto.length() == 5 ? texto + ":00" : texto);
            } catch (Exception e) {
                log.warn("Horário retornado pelo Gemini em formato inesperado: {}", texto);
            }
        }
        return CampoExtraido.<LocalTime>builder().valor(valor).confianca(valor != null ? confianca(campo) : null)
                .build();
    }

    private CampoExtraido<BigDecimal> campoValor(JsonNode raiz, String nomeCampo) {
        JsonNode campo = raiz.path(nomeCampo);
        JsonNode valorNode = campo.path("valor");
        BigDecimal valor = valorNode.isNumber() ? valorNode.decimalValue() : null;
        return CampoExtraido.<BigDecimal>builder().valor(valor).confianca(valor != null ? confianca(campo) : null)
                .build();
    }

    private Double confianca(JsonNode campo) {
        JsonNode confiancaNode = campo.path("confianca");
        return confiancaNode.isNumber() ? confiancaNode.asDouble() : null;
    }

    private ObjectNode montarResponseSchema() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "OBJECT");
        ObjectNode props = schema.putObject("properties");

        campoSchema(props, "nomeCliente", "STRING");
        campoSchema(props, "telefoneCliente", "STRING");
        campoSchema(props, "placaVeiculo", "STRING");
        campoSchema(props, "modeloVeiculo", "STRING");
        campoSchema(props, "descricaoServico", "STRING");
        campoSchema(props, "data", "STRING");
        campoSchema(props, "horario", "STRING");
        campoSchema(props, "valor", "NUMBER");

        ObjectNode observacoes = props.putObject("observacoesLivres");
        observacoes.put("type", "STRING");
        observacoes.put("nullable", true);

        schema.putArray("required").add("nomeCliente").add("telefoneCliente").add("placaVeiculo").add("modeloVeiculo")
                .add("descricaoServico").add("data").add("horario").add("valor");
        return schema;
    }

    private void campoSchema(ObjectNode props, String nome, String tipoValor) {
        ObjectNode campo = props.putObject(nome);
        campo.put("type", "OBJECT");
        ObjectNode campoProps = campo.putObject("properties");
        ObjectNode valorSchema = campoProps.putObject("valor");
        valorSchema.put("type", tipoValor);
        valorSchema.put("nullable", true);
        ObjectNode confiancaSchema = campoProps.putObject("confianca");
        confiancaSchema.put("type", "NUMBER");
        confiancaSchema.put("nullable", true);
        campo.putArray("required").add("valor").add("confianca");
    }
}
