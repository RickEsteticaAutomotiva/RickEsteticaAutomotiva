package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class CalendarEventRequest {

    @JsonProperty("summary")
    private String titulo;

    @JsonProperty("description")
    private String descricao;

    @JsonProperty("location")
    private String localizacao;

    @JsonProperty("startDateTime")
    private Instant dataHoraInicio;

    @JsonProperty("endDateTime")
    private Instant dataHoraFim;

    @JsonProperty("timeZone")
    private String fusoHorario;

    @JsonProperty("attendeeEmails")
    private List<String> emailsParticipantes;

    @JsonProperty("visibility")
    private String visibilidade;

    @JsonProperty("guestsCanSeeOtherGuests")
    private Boolean convidadosPodemVerOutrosConvidados;

    @JsonProperty("guestsCanInviteOthers")
    private Boolean convidadosPodemConvidarOutros;

    @JsonProperty("transparency")
    private String transparencia;
}