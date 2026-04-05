package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CadastrarServicoUseCase {

    private final ServicoGateway servicoGateway;

    public Servico execute(Servico servico) {
        return servicoGateway.salvar(servico);
    }
}
