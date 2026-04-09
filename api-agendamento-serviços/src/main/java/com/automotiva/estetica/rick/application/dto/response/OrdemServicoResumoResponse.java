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
public class OrdemServicoResumoResponse {

    private Long id;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataConclusao;
    private StatusResumoResponse status;
    private String observacoes;
    private BigDecimal valorTotal;
    private OrdemServicoClienteResumoResponse cliente;
    private OrdemServicoVeiculoResumoResponse veiculo;
    private List<OrdemServicoServicoResumoResponse> servicos;
}
