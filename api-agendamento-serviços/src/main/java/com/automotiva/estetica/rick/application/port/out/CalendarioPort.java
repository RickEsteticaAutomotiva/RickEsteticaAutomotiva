package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.application.dto.request.CalendarioEventoRequest;
import com.automotiva.estetica.rick.application.dto.response.CalendarioEventoResponse;

public interface CalendarioPort {

    CalendarioEventoResponse criarEvento(CalendarioEventoRequest request);

    CalendarioEventoResponse buscarEvento(String eventoId);

    CalendarioEventoResponse atualizarEvento(String eventoId, CalendarioEventoRequest request);

    void deletarEvento(String eventoId);
}
