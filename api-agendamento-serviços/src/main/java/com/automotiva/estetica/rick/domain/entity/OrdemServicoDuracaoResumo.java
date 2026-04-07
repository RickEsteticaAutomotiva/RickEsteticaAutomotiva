package com.automotiva.estetica.rick.domain.entity;

import java.time.LocalDateTime;

public record OrdemServicoDuracaoResumo(Long id, LocalDateTime dataAgendamento, Long duracaoTotal) {
}

