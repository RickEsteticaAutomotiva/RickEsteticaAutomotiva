package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.infra.ServicoConexaoGoogleCalendar;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class EventoGoogleCalendarService {

    private final ServicoConexaoGoogleCalendar servicoConexaoGoogleCalendar;

    public EventoGoogleCalendarService(ServicoConexaoGoogleCalendar servicoConexaoGoogleCalendar) {
        this.servicoConexaoGoogleCalendar = servicoConexaoGoogleCalendar;
    }

    public String criarEvento(String resumo, String descricao, Date inicio, Date fim) throws IOException {
        Calendar service = servicoConexaoGoogleCalendar.obterServicoCalendario();

        Event event = new Event()
                .setSummary(resumo)
                .setDescription(descricao);

        EventDateTime start = new EventDateTime()
                .setDateTime(new DateTime(inicio))
                .setTimeZone("America/Sao_Paulo");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(new DateTime(fim))
                .setTimeZone("America/Sao_Paulo");
        event.setEnd(end);

        Event eventoCriado = service.events().insert("primary", event).execute();
        return eventoCriado.getId();
    }

    public void atualizarEvento(String eventId, String novoResumo, String novaDescricao, Date inicio, Date fim) throws IOException {
        Calendar service = servicoConexaoGoogleCalendar.obterServicoCalendario();

        Event event = service.events().get("primary", eventId).execute();

        event.setSummary(novoResumo);
        event.setDescription(novaDescricao);
        event.setStart(new EventDateTime().setDateTime(new DateTime(inicio)).setTimeZone("America/Sao_Paulo"));
        event.setEnd(new EventDateTime().setDateTime(new DateTime(fim)).setTimeZone("America/Sao_Paulo"));

        service.events().update("primary", eventId, event).execute();
    }

    public void deletarEvento(String eventId) throws IOException {
        Calendar service = servicoConexaoGoogleCalendar.obterServicoCalendario();
        service.events().delete("primary", eventId).execute();
    }
}
