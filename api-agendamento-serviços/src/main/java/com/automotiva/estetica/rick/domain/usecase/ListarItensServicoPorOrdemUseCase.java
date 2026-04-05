package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListarItensServicoPorOrdemUseCase {

    private final ItemServicoGateway itemServicoGateway;

    public List<ItemServico> execute(Long ordemServicoId) {
        return itemServicoGateway.buscarPorOrdemServicoId(ordemServicoId);
    }
}
