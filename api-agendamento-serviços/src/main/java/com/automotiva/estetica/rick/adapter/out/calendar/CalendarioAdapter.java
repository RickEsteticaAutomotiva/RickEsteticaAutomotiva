package com.automotiva.estetica.rick.adapter.out.calendar;

import com.automotiva.estetica.rick.application.dto.request.CalendarioEventoRequest;
import com.automotiva.estetica.rick.application.dto.response.CalendarioEventoResponse;
import com.automotiva.estetica.rick.application.port.out.CalendarioPort;
import com.automotiva.estetica.rick.domain.exception.IntegracaoException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!integration-test")
@RequiredArgsConstructor
public class CalendarioAdapter implements CalendarioPort {

    private final GoogleCalendarConexao conexao;

    private static final String CALENDARIO = "primary";
    private static final DateTimeFormatter RFC3339 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Override
    public CalendarioEventoResponse criarEvento(CalendarioEventoRequest request) {
        try {
            Calendar servico = conexao.obterServico();
            Event evento = montarEvento(request);
            return toResponse(servico.events().insert(CALENDARIO, evento).execute());
        } catch (IOException e) {
            log.error("Erro ao criar evento no Google Calendar: {}", e.getMessage());
            throw IntegracaoException.builder()
                    .mensagem("Falha ao criar evento no Google Calendar")
                    .detalhes(e.getMessage())
                    .build();
        }
    }

    @Override
    public CalendarioEventoResponse buscarEvento(String eventoId) {
        try {
            return toResponse(
                    conexao.obterServico().events().get(CALENDARIO, eventoId).execute());
        } catch (IOException e) {
            throw RecursoNaoEncontradoException.builder()
                    .mensagem("Evento " + eventoId + " não encontrado no Google Calendar")
                    .detalhes(e.getMessage())
                    .build();
        }
    }

    @Override
    public CalendarioEventoResponse atualizarEvento(String eventoId, CalendarioEventoRequest request) {
        try {
            Event evento = montarEvento(request);
            return toResponse(conexao.obterServico()
                    .events()
                    .update(CALENDARIO, eventoId, evento)
                    .execute());
        } catch (IOException e) {
            log.error("Erro ao atualizar evento {}: {}", eventoId, e.getMessage());
            throw IntegracaoException.builder()
                    .mensagem("Falha ao atualizar evento no Google Calendar")
                    .detalhes(e.getMessage())
                    .build();
        }
    }

    @Override
    public void deletarEvento(String eventoId) {
        try {
            conexao.obterServico().events().delete(CALENDARIO, eventoId).execute();
        } catch (IOException e) {
            log.error("Erro ao deletar evento {}: {}", eventoId, e.getMessage());
            throw IntegracaoException.builder()
                    .mensagem("Falha ao deletar evento no Google Calendar")
                    .detalhes(e.getMessage())
                    .build();
        }
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private CalendarioEventoResponse toResponse(Event event) {
        Instant inicio = event.getStart() != null && event.getStart().getDateTime() != null
                ? Instant.ofEpochMilli(event.getStart().getDateTime().getValue())
                : null;
        Instant fim = event.getEnd() != null && event.getEnd().getDateTime() != null
                ? Instant.ofEpochMilli(event.getEnd().getDateTime().getValue())
                : null;
        return new CalendarioEventoResponse(
                event.getId(), event.getSummary(), event.getDescription(), event.getLocation(), inicio, fim);
    }

    private Event montarEvento(CalendarioEventoRequest request) {
        String fuso = request.getFusoHorario() != null ? request.getFusoHorario() : "America/Sao_Paulo";

        ZonedDateTime inicio = request.getDataHoraInicio().atZone(ZoneId.of(fuso));
        ZonedDateTime fim = request.getDataHoraFim().atZone(ZoneId.of(fuso));

        Event evento = new Event()
                .setSummary(request.getTitulo())
                .setDescription(request.getDescricao())
                .setLocation(request.getLocalizacao());

        evento.setStart(new EventDateTime()
                .setDateTime(new DateTime(RFC3339.format(inicio)))
                .setTimeZone(fuso));
        evento.setEnd(new EventDateTime()
                .setDateTime(new DateTime(RFC3339.format(fim)))
                .setTimeZone(fuso));

        if (request.getEmailsParticipantes() != null
                && !request.getEmailsParticipantes().isEmpty()) {
            List<EventAttendee> participantes = request.getEmailsParticipantes().stream()
                    .map(e -> new EventAttendee().setEmail(e))
                    .toList();
            evento.setAttendees(participantes);
        }

        if (request.getVisibilidade() != null) evento.setVisibility(request.getVisibilidade());
        if (request.getTransparencia() != null) evento.setTransparency(request.getTransparencia());

        return evento;
    }
}
