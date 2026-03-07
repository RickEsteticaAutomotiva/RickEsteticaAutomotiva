package com.automotiva.estetica.rick.application.dto.request.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record OrdemServicoCriadaEvent(
        Long IdOrdemServico,
        String placaVeiculo,
        LocalDateTime dataAgendamento,
        List<String> servicos,
        String observacoes)
        implements Serializable {}
