package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.assistente.MensagemAssistente;
import com.automotiva.estetica.rick.domain.entity.assistente.RespostaAssistente;
import java.util.List;

/**
 * Porta de saída para um provedor de IA com tool/function calling. A
 * implementação concreta (ex: Gemini) fica em infrastructure/gateway — trocar
 * de provedor não deve exigir nenhuma mudança em domain/usecase ou
 * application.
 */
public interface AiAssistantGateway {

    RespostaAssistente enviarMensagem(List<MensagemAssistente> historico);
}
