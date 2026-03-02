package com.automotiva.estetica.rick.application.dto.request;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarioEventoRequest {

    private String titulo;
    private String descricao;
    private String localizacao;
    private Instant dataHoraInicio;
    private Instant dataHoraFim;
    private String fusoHorario;
    private List<String> emailsParticipantes;
    private String visibilidade;
    private Boolean convidadosPodemVerOutrosConvidados;
    private Boolean convidadosPodemConvidarOutros;
    private String transparencia;
}
