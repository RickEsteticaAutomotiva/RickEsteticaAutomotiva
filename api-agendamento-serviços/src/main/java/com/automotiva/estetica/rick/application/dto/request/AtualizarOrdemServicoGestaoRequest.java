package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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
public class AtualizarOrdemServicoGestaoRequest {

    // Data de agendamento é opcional - pode ser atualizada junto com
    // status/observações
    private LocalDateTime dataAgendamento;

    @Size(max = 255, message = "Observações não pode exceder 255 caracteres")
    private String observacoes;

    @NotNull(message = "Status é obrigatório")
    @Min(value = 1, message = "Status inválido")
    @Max(value = 5, message = "Status inválido")
    private Long status;
}
