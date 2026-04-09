package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletarVeiculoUseCase {

    private final VeiculoGateway veiculoGateway;

    public void execute(Long id) {
        if (!veiculoGateway.existePorId(id)) {
            throw RecursoNaoEncontradoException.builder().mensagem("Veiculo com id " + id + " não foi encontrado")
                    .detalhes("").build();
        }
        veiculoGateway.deletarPorId(id);
    }
}
