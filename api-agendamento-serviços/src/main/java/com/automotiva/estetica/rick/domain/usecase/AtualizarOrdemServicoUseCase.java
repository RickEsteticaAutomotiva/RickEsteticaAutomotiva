package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoEventGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizarOrdemServicoUseCase {

    private final BuscarOrdemServicoComDetalhesUseCase buscarOrdemServicoComDetalhesUseCase;
    private final OrdemServicoEventGateway ordemServicoEventGateway;
    private final OrdemServicoGateway ordemServicoGateway;

    public OrdemServico execute(Long ordemServicoId, LocalDateTime dataAgendamento, BigDecimal precoMinimo,
            String observacoes, Long statusId, Long motivoId) {
        OrdemServico ordemServico = buscarOrdemServicoComDetalhesUseCase.execute(ordemServicoId);
        ordemServico.atualizar(dataAgendamento, precoMinimo, observacoes, statusId, motivoId);

        ordemServicoEventGateway.publicarOrdemServicoAtualizada(ordemServico);

        return ordemServicoGateway.salvar(ordemServico);
    }
}
