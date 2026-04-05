package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CadastrarVeiculoUseCase {

    private final VeiculoGateway veiculoGateway;

    public Veiculo execute(Veiculo veiculo) {
        return veiculoGateway.salvar(veiculo);
    }
}
