package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoogleCalendarEvent {
    private String id;
    private String summary;
    private String description;
    private String location;

    @JsonProperty("start")
    private EventDateTime startDateTime;

    @JsonProperty("end")
    private EventDateTime endDateTime;

    private List<EventAttendee> attendees;
    private String status;
    private String htmlLink;

    @Data
    public static class EventDateTime {
        private LocalDateTime dateTime;
        private String timeZone;
    }

    @Data
    public static class EventAttendee {
        private String email;
        private String displayName;
        private Boolean optional;
    }
}
