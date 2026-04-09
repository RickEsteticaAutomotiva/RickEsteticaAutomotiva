package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarOrdemServicoComDetalhesUseCase {

    private final OrdemServicoGateway ordemServicoGateway;

    public OrdemServico execute(Long ordemServicoId) {
        return ordemServicoGateway.buscarPorIdComDetalhes(ordemServicoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("a ordem de serviço com id " + ordemServicoId + " não foi encontrada").detalhes("")
                        .build());
    }
}
