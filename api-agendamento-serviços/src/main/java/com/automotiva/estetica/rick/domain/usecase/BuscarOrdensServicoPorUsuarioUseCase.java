package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarOrdensServicoPorUsuarioUseCase {

    private final OrdemServicoGateway ordemServicoGateway;

    public List<OrdemServico> execute(Long usuarioId) {
        return ordemServicoGateway.buscarPorVeiculoPessoaId(usuarioId);
    }
}
