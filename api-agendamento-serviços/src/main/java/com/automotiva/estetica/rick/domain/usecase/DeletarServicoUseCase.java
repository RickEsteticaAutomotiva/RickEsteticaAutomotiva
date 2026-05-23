package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletarServicoUseCase {

    private final ServicoGateway servicoGateway;

    public void execute(Long id) {
        if (!servicoGateway.existePorId(id)) {
            throw RecursoNaoEncontradoException.builder().mensagem("o serviço com id " + id + " não foi encontrado")
                    .detalhes("").build();
        }
        servicoGateway.deletarPorId(id);
    }
}
