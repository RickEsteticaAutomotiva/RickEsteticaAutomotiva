package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizarStatusOrdemServicoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;

    public OrdemServico execute(Long ordemServicoId, Long statusId) {
        if (statusId == null) {
            throw CampoInvalidoException.builder().mensagem("status é obrigatório").detalhes("").build();
        }

        OrdemServico ordemServico = ordemServicoGateway.buscarPorIdComDetalhes(ordemServicoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("a ordem de serviço com id " + ordemServicoId + " não foi encontrada").detalhes("")
                        .build());

        ordemServico.atualizar(null, null, null, statusId, null);
        return ordemServicoGateway.salvar(ordemServico);
    }
}
