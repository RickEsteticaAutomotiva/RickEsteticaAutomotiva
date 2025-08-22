package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CalendarEventRequest {
    private String summary;
    private String description;
    private String location;
    private Instant startDateTime;
    private Instant  endDateTime;
    private String timeZone;
    private List<String> attendeeEmails;
}