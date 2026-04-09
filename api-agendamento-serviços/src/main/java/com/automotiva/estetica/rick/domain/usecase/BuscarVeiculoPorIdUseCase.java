package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarVeiculoPorIdUseCase {

    private final VeiculoGateway veiculoGateway;

    public Veiculo execute(Long id) {
        return veiculoGateway.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("Veiculo com id " + id + " não foi encontrado").detalhes("").build());
    }
}
