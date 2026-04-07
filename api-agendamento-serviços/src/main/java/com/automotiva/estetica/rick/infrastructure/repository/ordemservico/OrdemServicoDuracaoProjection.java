package com.automotiva.estetica.rick.infrastructure.repository.ordemservico;

import java.time.LocalDateTime;

public interface OrdemServicoDuracaoProjection {

    Long getId();

    LocalDateTime getDataAgendamento();

    Long getDuracaoTotal();
}

