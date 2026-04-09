package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListarVeiculosUseCase {

    private final VeiculoGateway veiculoGateway;

    public List<Veiculo> execute() {
        return veiculoGateway.buscarTodos();
    }
}
