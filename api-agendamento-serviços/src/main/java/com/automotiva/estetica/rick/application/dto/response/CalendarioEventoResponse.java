package com.automotiva.estetica.rick.application.dto.response;

import java.time.Instant;

/**
 * DTO de resposta da porta CalendarioPort. Desacopla a camada de aplicação da
 * biblioteca Google Calendar, eliminando a dependência de
 * com.google.api.services.calendar.model.Event na interface de porta de saída.
 */
public record CalendarioEventoResponse(String eventoId, String titulo, String descricao, String localizacao,
        Instant dataHoraInicio, Instant dataHoraFim) {
}
