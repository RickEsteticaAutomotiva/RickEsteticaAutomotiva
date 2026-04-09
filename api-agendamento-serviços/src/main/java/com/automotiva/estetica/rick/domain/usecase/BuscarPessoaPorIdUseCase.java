package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use Case para buscar Pessoa por ID.
 */
@Service
@RequiredArgsConstructor
public class BuscarPessoaPorIdUseCase {

    private final PessoaGateway pessoaGateway;

    public Pessoa execute(Long id) {
        return pessoaGateway.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("a pessoa com id " + id + " não foi encontrada").detalhes("").build());
    }
}
