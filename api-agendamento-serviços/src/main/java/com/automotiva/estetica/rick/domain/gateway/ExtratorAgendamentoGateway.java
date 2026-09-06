package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.DadosAgendamentoExtraido;
import java.time.LocalDate;

/**
 * Porta de saída para um provedor de IA multimodal capaz de extrair dados
 * estruturados de agendamento a partir de uma imagem (anotação, print de
 * WhatsApp, etc.). A implementação concreta (ex.: Gemini) fica em
 * infrastructure/gateway — trocar de provedor não deve exigir nenhuma mudança
 * em domain/usecase ou application.
 *
 * <p>
 * Diferente do {@link AiAssistantGateway} (chat com histórico e
 * tool-calling), esta porta é single-shot: uma imagem entra, um
 * {@link DadosAgendamentoExtraido} estruturado sai.
 */
public interface ExtratorAgendamentoGateway {

    /**
     * @param imagemBase64
     *            conteúdo da imagem, codificado em base64
     * @param mimeType
     *            tipo MIME da imagem (ex.: "image/jpeg")
     * @param dataReferencia
     *            data a ser usada pela IA para resolver expressões relativas
     *            ("amanhã", "sexta") — nunca deve ser assumida pelo provedor
     * @return dados extraídos, com {@code null} em qualquer campo não
     *         identificável com segurança
     */
    DadosAgendamentoExtraido extrair(String imagemBase64, String mimeType, LocalDate dataReferencia);
}
