package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListarServicosUseCase {

    private final ServicoGateway servicoGateway;

    public Page<Servico> execute(String filtro, Pageable pageable) {
        return servicoGateway.buscarTodos(filtro, pageable);
    }
}
