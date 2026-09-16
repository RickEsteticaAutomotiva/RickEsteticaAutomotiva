package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.assistente.MensagemAssistente;
import com.automotiva.estetica.rick.domain.entity.assistente.RespostaAssistente;
import com.automotiva.estetica.rick.domain.gateway.AiAssistantGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnviarMensagemAssistenteUseCase {

    private final AiAssistantGateway aiAssistantGateway;

    public RespostaAssistente execute(List<MensagemAssistente> historico) {
        return aiAssistantGateway.enviarMensagem(historico);
    }
}
