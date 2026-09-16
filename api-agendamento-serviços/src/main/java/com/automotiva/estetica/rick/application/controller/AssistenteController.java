package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.dto.request.AssistenteMensagemRequest;
import com.automotiva.estetica.rick.application.dto.response.AssistenteMensagemResponse;
import com.automotiva.estetica.rick.application.mapper.AssistenteDTOMapper;
import com.automotiva.estetica.rick.application.security.GerenteOnly;
import com.automotiva.estetica.rick.domain.entity.assistente.RespostaAssistente;
import com.automotiva.estetica.rick.domain.usecase.EnviarMensagemAssistenteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Proxy autenticado e stateless para o provedor de IA do assistente de
 * agendamento — feature exclusiva do gerente, que agenda serviços em nome de
 * clientes. Não guarda estado de conversa nem executa nenhuma regra de
 * negócio de agendamento — o app reenvia o histórico completo a cada
 * chamada, e as tools (buscarServicos, buscarVeiculos,
 * buscarHorariosDisponiveis, criarAgendamento) são executadas pelo app contra
 * os endpoints de gestão já existentes.
 */
@RestController
@RequestMapping("/assistente")
@RequiredArgsConstructor
@GerenteOnly
@Tag(name = "Assistente IA", description = "Proxy autenticado para o assistente de agendamento do gerente com IA")
public class AssistenteController {

    private final EnviarMensagemAssistenteUseCase enviarMensagemAssistenteUseCase;
    private final AssistenteDTOMapper assistenteDTOMapper;

    @PostMapping("/mensagens")
    @Operation(summary = "Envia o histórico da conversa ao assistente e recebe a próxima resposta (texto e/ou chamadas de função)")
    public ResponseEntity<AssistenteMensagemResponse> enviarMensagem(
            @Valid @RequestBody AssistenteMensagemRequest request) {
        RespostaAssistente resposta = enviarMensagemAssistenteUseCase
                .execute(assistenteDTOMapper.toDomain(request.getHistorico()));
        return ResponseEntity.ok(assistenteDTOMapper.toResponse(resposta));
    }
}
