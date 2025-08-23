package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CalendarEventRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class GoogleCalendarService {

    @Value("${google.calendar.credentials.file:classpath:credentials.json}")
    private String credentialsFile;

    @Value("${google.calendar.application.name:ApiAgendamentoServices}")
    private String applicationName;

    private Calendar calendarService;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final DateTimeFormatter RFC3339_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @PostConstruct
    public void init() {
        try {
            this.calendarService = createCalendarService();
            log.info("Google Calendar Service initialized successfully");
        } catch (IOException | GeneralSecurityException e) {
            log.error("Failed to initialize Google Calendar Service", e);
            throw new RuntimeException("Failed to initialize Google Calendar Service", e);
        }
    }

    private Calendar createCalendarService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Resource resource = new ClassPathResource(credentialsFile.replace("classpath:", ""));
        if (!resource.exists()) {
            throw new IOException("Credentials file not found: " + credentialsFile);
        }

        try (InputStream credentialsStream = resource.getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                    .createScoped(SCOPES);

            return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                    .setApplicationName(applicationName)
                    .build();
        }
    }

    public Event createEvent(CalendarEventRequest request) throws IOException {
        try {
            Event event = new Event()
                    .setSummary(request.getTitulo())
                    .setDescription(request.getDescricao())
                    .setLocation(request.getLocalizacao());

            // Converte para o formato RFC 3339 correto
            String startRfc3339 = convertToRfc3339(request.getDataHoraInicio(), request.getFusoHorario());
            String endRfc3339 = convertToRfc3339(request.getDataHoraFim(), request.getFusoHorario());

            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startRfc3339))
                    .setTimeZone(request.getFusoHorario());
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endRfc3339))
                    .setTimeZone(request.getFusoHorario());
            event.setEnd(end);

            // Novos campos adicionados
            if (request.getVisibilidade() != null) {
                event.setVisibility(request.getVisibilidade());
            }

            if (request.getConvidadosPodemVerOutrosConvidados() != null) {
                event.setGuestsCanSeeOtherGuests(request.getConvidadosPodemVerOutrosConvidados());
            }

            if (request.getConvidadosPodemConvidarOutros() != null) {
                event.setGuestsCanInviteOthers(request.getConvidadosPodemConvidarOutros());
            }

            if (request.getTransparencia() != null) {
                event.setTransparency(request.getTransparencia());
            }

            if (request.getEmailsParticipantes() != null && !request.getEmailsParticipantes().isEmpty()) {
                List<EventAttendee> attendees = request.getEmailsParticipantes().stream()
                        .map(email -> new EventAttendee().setEmail(email))
                        .collect(Collectors.toList());
                event.setAttendees(attendees);
            }

            return calendarService.events().insert("primary", event).execute();
        } catch (IOException e) {
            log.error("Error creating calendar event", e);
            throw e;
        }
    }

    private String convertToRfc3339(Instant instant, String timeZone) {
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(timeZone));
        return zonedDateTime.format(RFC3339_FORMATTER);
    }

    public Event getEvent(String eventId) throws IOException {
        try {
            return calendarService.events().get("primary", eventId).execute();
        } catch (IOException e) {
            log.error("Error getting calendar event: {}", eventId, e);
            throw e;
        }
    }

    public List<Event> listEvents() throws IOException {
        try {
            return calendarService.events().list("primary")
                    .setMaxResults(10)
                    .execute()
                    .getItems();
        } catch (IOException e) {
            log.error("Error listing calendar events", e);
            throw e;
        }
    }

    public Event updateEvent(String eventId, CalendarEventRequest request) throws IOException {
        try {
            Event existingEvent = calendarService.events().get("primary", eventId).execute();

            // Atualiza campos básicos
            existingEvent.setSummary(request.getTitulo())
                    .setDescription(request.getDescricao())
                    .setLocation(request.getLocalizacao());

            // Atualiza novos campos de configuração
            if (request.getVisibilidade() != null) {
                existingEvent.setVisibility(request.getVisibilidade());
            }

            if (request.getConvidadosPodemVerOutrosConvidados() != null) {
                existingEvent.setGuestsCanSeeOtherGuests(request.getConvidadosPodemVerOutrosConvidados());
            }

            if (request.getConvidadosPodemConvidarOutros() != null) {
                existingEvent.setGuestsCanInviteOthers(request.getConvidadosPodemConvidarOutros());
            }

            if (request.getTransparencia() != null) {
                existingEvent.setTransparency(request.getTransparencia());
            }

            // Atualiza datas e horários
            if (request.getDataHoraInicio() != null && request.getFusoHorario() != null) {
                String startRfc3339 = convertToRfc3339(request.getDataHoraInicio(), request.getFusoHorario());
                EventDateTime start = new EventDateTime()
                        .setDateTime(new com.google.api.client.util.DateTime(startRfc3339))
                        .setTimeZone(request.getFusoHorario());
                existingEvent.setStart(start);
            }

            if (request.getDataHoraFim() != null && request.getFusoHorario() != null) {
                String endRfc3339 = convertToRfc3339(request.getDataHoraFim(), request.getFusoHorario());
                EventDateTime end = new EventDateTime()
                        .setDateTime(new com.google.api.client.util.DateTime(endRfc3339))
                        .setTimeZone(request.getFusoHorario());
                existingEvent.setEnd(end);
            }

            // Atualiza participantes
            if (request.getEmailsParticipantes() != null) {
                if (request.getEmailsParticipantes().isEmpty()) {
                    // Se lista vazia, remove todos os participantes
                    existingEvent.setAttendees(null);
                } else {
                    // Atualiza com nova lista de participantes
                    List<EventAttendee> attendees = request.getEmailsParticipantes().stream()
                            .map(email -> new EventAttendee().setEmail(email))
                            .collect(Collectors.toList());
                    existingEvent.setAttendees(attendees);
                }
            }

            return calendarService.events().update("primary", eventId, existingEvent).execute();

        } catch (IOException e) {
            log.error("Error updating calendar event: {}", eventId, e);
            throw e;
        }
    }

    public void deleteEvent(String eventId) throws IOException {
        try {
            calendarService.events().delete("primary", eventId).execute();
        } catch (IOException e) {
            log.error("Error deleting calendar event: {}", eventId, e);
            throw e;
        }
    }

    public boolean isServiceAvailable() {
        return calendarService != null;
    }
}