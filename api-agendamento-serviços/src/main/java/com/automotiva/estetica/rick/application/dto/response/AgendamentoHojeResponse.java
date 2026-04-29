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
public class AgendamentoHojeResponse {

    private Long id;
    private LocalDateTime dataHora;
    private String status;
    private AgendamentoHojeClienteResponse cliente;
    private AgendamentoHojeVeiculoResponse veiculo;
    private List<AgendamentoHojeServicoResponse> servicos;
    private BigDecimal precoMinimo;
    private BigDecimal precoTotal;
    private String observacoes;
}

