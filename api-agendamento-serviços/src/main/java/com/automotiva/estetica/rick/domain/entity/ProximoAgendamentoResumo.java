package com.automotiva.estetica.rick.domain.entity;

import java.time.LocalDateTime;

public record ProximoAgendamentoResumo(Long ordemServicoId, String servico, LocalDateTime dataAgendamento,
        String clienteNome, String veiculoMarca, String veiculoModelo, String veiculoPlaca, Long status) {
}
