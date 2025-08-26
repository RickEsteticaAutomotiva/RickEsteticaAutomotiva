package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoogleCalendarEvent {

    @JsonProperty("id")
    private String id;

    @JsonProperty("summary")
    private String titulo;

    @JsonProperty("description")
    private String descricao;

    @JsonProperty("location")
    private String localizacao;

    @JsonProperty("start")
    private EventDateTime dataHoraInicio;

    @JsonProperty("end")
    private EventDateTime dataHoraFim;

    @JsonProperty("attendees")
    private List<EventAttendee> participantes;

    @JsonProperty("status")
    private String status;

    @JsonProperty("htmlLink")
    private String linkHtml;

    @JsonProperty("visibility")
    private String visibilidade;

    @JsonProperty("guestsCanSeeOtherGuests")
    private Boolean convidadosPodemVerOutrosConvidados;

    @JsonProperty("guestsCanInviteOthers")
    private Boolean convidadosPodemConvidarOutros;

    @JsonProperty("transparency")
    private String transparencia;

    @Data
    public static class EventDateTime {
        @JsonProperty("dateTime")
        private LocalDateTime dataHora;

        @JsonProperty("timeZone")
        private String fusoHorario;
    }

    @Data
    public static class EventAttendee {
        @JsonProperty("email")
        private String email;

        @JsonProperty("displayName")
        private String nomeExibicao;

        @JsonProperty("optional")
        private Boolean opcional;
    }
}