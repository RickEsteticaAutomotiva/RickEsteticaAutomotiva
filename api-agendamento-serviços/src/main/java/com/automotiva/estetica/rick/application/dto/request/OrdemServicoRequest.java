package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class OrdemServicoRequest {

    @NotNull(message = "A data de agendamento é obrigatória")
    private LocalDateTime dataAgendamento;

    private List<Long> servicos;

    private BigDecimal precoMinimo;

    @NotNull(message = "O veículo é obrigatório")
    private Long veiculo;

    private Long status;

    private String observacoes;

    private Long motivo;
}
