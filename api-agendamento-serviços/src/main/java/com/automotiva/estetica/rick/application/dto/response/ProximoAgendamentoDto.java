package com.automotiva.estetica.rick.application.dto.response;

import java.time.LocalDateTime;

/**
 * DTO da porta de saída para o próximo agendamento do dia no dashboard.
 */
public record ProximoAgendamentoDto(
        Long ordemServicoId,
        String servico,
        LocalDateTime dataAgendamento,
        String clienteNome,
        String veiculoMarca,
        String veiculoModelo,
        String veiculoPlaca,
        Long status) {}

