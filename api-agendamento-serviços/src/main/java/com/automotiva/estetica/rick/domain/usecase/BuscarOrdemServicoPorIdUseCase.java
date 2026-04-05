package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarOrdemServicoPorIdUseCase {

    private final OrdemServicoGateway ordemServicoGateway;

    public OrdemServico execute(Long id) {
        return ordemServicoGateway.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("a ordem de serviço com id " + id + " não foi encontrada").detalhes("").build());
    }
}
