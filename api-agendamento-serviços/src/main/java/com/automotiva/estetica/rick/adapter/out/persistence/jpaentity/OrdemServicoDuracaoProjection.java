package com.automotiva.estetica.rick.adapter.out.persistence.jpaentity;

import java.time.LocalDateTime;

public interface OrdemServicoDuracaoProjection {
    Long getId();
    LocalDateTime getDataAgendamento();
    Long getDuracaoTotal();
}
