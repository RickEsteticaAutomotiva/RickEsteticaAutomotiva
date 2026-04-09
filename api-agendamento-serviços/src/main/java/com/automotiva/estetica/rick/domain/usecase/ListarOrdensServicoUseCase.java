package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListarOrdensServicoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;

    public Page<OrdemServico> execute(String filtro, Pageable pageable) {
        return ordemServicoGateway.buscarTodos(filtro, pageable);
    }
}
