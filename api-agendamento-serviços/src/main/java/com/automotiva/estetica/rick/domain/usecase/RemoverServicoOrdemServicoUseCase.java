package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoverServicoOrdemServicoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ItemServicoGateway itemServicoGateway;

    public OrdemServico execute(Long ordemServicoId, Long servicoId) {
        OrdemServico ordemServico = ordemServicoGateway.buscarPorIdComDetalhes(ordemServicoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("a ordem de serviço com id " + ordemServicoId + " não foi encontrada").detalhes("")
                        .build());

        ItemServico itemServico = itemServicoGateway.buscarPorOrdemServicoIdEServicoId(ordemServicoId, servicoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("o serviço " + servicoId + " não foi encontrado na ordem " + ordemServicoId)
                        .detalhes("").build());

        itemServicoGateway.removerPorId(itemServico.getId());
        return ordemServico;
    }
}
