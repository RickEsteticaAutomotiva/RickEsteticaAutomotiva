package com.automotiva.estetica.rick.application.dto.response;

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
public class OrdemServicoResponse {

    private Long id;
    private LocalDateTime dataAgendamento;
    private List<Long> servicos;
    private BigDecimal precoMinimo;
    private Long veiculo;
    private Long status;
    private String observacoes;
    private LocalDateTime dtConclusao;
    private Long motivo;
    private OrdemServicoClienteResumoResponse cliente;
}
