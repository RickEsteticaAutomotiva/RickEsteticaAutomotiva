package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CalendarEventRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.ServicoConexaoGoogleCalendar;
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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarioGoogleService {

    private final ServicoConexaoGoogleCalendar servicoConexao;

    private static final DateTimeFormatter FORMATADOR_RFC3339 =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static final String CALENDARIO_PRIMARIO = "primary";

    public Event criarEvento(CalendarEventRequest request) {
        try {
            Calendar servicoCalendario = servicoConexao.obterServicoCalendario();

            Event evento = new Event()
                    .setSummary(request.getTitulo())
                    .setDescription(request.getDescricao())
                    .setLocation(request.getLocalizacao());

            String inicioRfc3339 = converterParaRfc3339(request.getDataHoraInicio(), request.getFusoHorario());
            String fimRfc3339 = converterParaRfc3339(request.getDataHoraFim(), request.getFusoHorario());

            EventDateTime inicio = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(inicioRfc3339))
                    .setTimeZone(request.getFusoHorario());
            evento.setStart(inicio);

            EventDateTime fim = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(fimRfc3339))
                    .setTimeZone(request.getFusoHorario());
            evento.setEnd(fim);

            configurarPropriedadesAdicionais(evento, request);

            return servicoCalendario
                    .events()
                    .insert(CALENDARIO_PRIMARIO, evento)
                    .execute();

        } catch (IOException e) {
            log.error("Erro ao criar evento no calendário", e);
            throw new RuntimeException("Erro ao criar evento");
        }
    }

    public Event obterEvento(String idEvento) {
        try {
            Calendar servicoCalendario = servicoConexao.obterServicoCalendario();

            return servicoCalendario.events().get(CALENDARIO_PRIMARIO, idEvento).execute();

        } catch (IOException e) {
            log.error("Erro ao obter evento do calendário: {}", idEvento, e);
            throw new RuntimeException("Erro ao buscar o calendário!");
        }
    }

    public List<Event> listarEventos() {
        return listarEventos(10);
    }

    public List<Event> listarEventos(int maxResultados) {
        try {
            Calendar servicoCalendario = servicoConexao.obterServicoCalendario();
            List<Event> eventos = servicoCalendario
                    .events()
                    .list(CALENDARIO_PRIMARIO)
                    .setMaxResults(maxResultados)
                    .execute()
                    .getItems();

            if (eventos.isEmpty()) {
                throw RecursoNaoEncontradaException.builder()
                        .mensagem("não foram encontrados eventos")
                        .detalhes("")
                        .build();
            }

            return eventos;

        } catch (IOException e) {
            log.error("Erro ao listar eventos do calendário", e);
            throw new RuntimeException("Erro ao listar eventos");
        }
    }

    public Event atualizarEvento(String idEvento, CalendarEventRequest request) {
        try {
            Calendar servicoCalendario = servicoConexao.obterServicoCalendario();
            Event eventoExistente = servicoCalendario
                    .events()
                    .get(CALENDARIO_PRIMARIO, idEvento)
                    .execute();

            configurarPropriedadesAdicionais(eventoExistente, request);

            if (request.getDataHoraInicio() != null && request.getFusoHorario() != null) {
                String inicioRfc3339 = converterParaRfc3339(request.getDataHoraInicio(), request.getFusoHorario());
                EventDateTime inicio = new EventDateTime()
                        .setDateTime(new com.google.api.client.util.DateTime(inicioRfc3339))
                        .setTimeZone(request.getFusoHorario());
                eventoExistente.setStart(inicio);
            }

            if (request.getDataHoraFim() != null && request.getFusoHorario() != null) {
                String fimRfc3339 = converterParaRfc3339(request.getDataHoraFim(), request.getFusoHorario());
                EventDateTime fim = new EventDateTime()
                        .setDateTime(new com.google.api.client.util.DateTime(fimRfc3339))
                        .setTimeZone(request.getFusoHorario());
                eventoExistente.setEnd(fim);
            }

            if (request.getDescricao() != null) {
                eventoExistente.setDescription(request.getDescricao());
            }

            atualizarParticipantes(eventoExistente, request);

            return servicoCalendario
                    .events()
                    .update(CALENDARIO_PRIMARIO, idEvento, eventoExistente)
                    .execute();

        } catch (IOException e) {
            log.error("Erro ao atualizar evento do calendário: {}", idEvento, e);
            throw new RuntimeException("Falha ao tentar atualizar evento");
        }
    }

    public void excluirEvento(String idEvento) {
        try {
            Calendar servicoCalendario = servicoConexao.obterServicoCalendario();
            servicoCalendario.events().delete(CALENDARIO_PRIMARIO, idEvento).execute();
            log.info("Evento excluído com sucesso: {}", idEvento);
        } catch (IOException e) {
            log.error("Erro ao excluir evento do calendário: {}", idEvento, e);
        }
    }

    public boolean estaDisponivel() {
        return servicoConexao.estaDisponivel();
    }

    // Métodos auxiliares mantidos iguais
    private void configurarPropriedadesAdicionais(Event evento, CalendarEventRequest request) {
        if (request.getVisibilidade() != null) {
            evento.setVisibility(request.getVisibilidade());
        }

        if (request.getConvidadosPodemVerOutrosConvidados() != null) {
            evento.setGuestsCanSeeOtherGuests(request.getConvidadosPodemVerOutrosConvidados());
        }

        if (request.getConvidadosPodemConvidarOutros() != null) {
            evento.setGuestsCanInviteOthers(request.getConvidadosPodemConvidarOutros());
        }

        if (request.getTransparencia() != null) {
            evento.setTransparency(request.getTransparencia());
        }

        if (request.getEmailsParticipantes() != null
                && !request.getEmailsParticipantes().isEmpty()) {
            List<EventAttendee> participantes = request.getEmailsParticipantes().stream()
                    .map(email -> new EventAttendee().setEmail(email))
                    .collect(Collectors.toList());
            evento.setAttendees(participantes);
        }
    }

    private void atualizarParticipantes(Event evento, CalendarEventRequest request) {
        if (request.getEmailsParticipantes() != null) {
            if (request.getEmailsParticipantes().isEmpty()) {
                evento.setAttendees(null);
            } else {
                List<EventAttendee> participantes = request.getEmailsParticipantes().stream()
                        .map(email -> new EventAttendee().setEmail(email))
                        .collect(Collectors.toList());
                evento.setAttendees(participantes);
            }
        }
    }

    private String converterParaRfc3339(Instant instant, String fusoHorario) {
        ZonedDateTime dataComFuso = instant.atZone(ZoneId.of(fusoHorario));
        return dataComFuso.format(FORMATADOR_RFC3339);
    }

    public void renovarCredenciais() throws IOException {
        try {
            servicoConexao.renovarCredenciais();
            log.info("Credenciais do serviço de calendário renovadas com sucesso");
        } catch (Exception e) {
            log.error("Erro ao renovar credenciais do serviço de calendário", e);
            throw new IOException("Erro ao renovar credenciais", e);
        }
    }
}
