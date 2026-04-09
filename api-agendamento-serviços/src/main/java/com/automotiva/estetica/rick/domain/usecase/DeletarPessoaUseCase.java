package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use Case para deletar uma Pessoa.
 */
@Service
@RequiredArgsConstructor
public class DeletarPessoaUseCase {

    private final PessoaGateway pessoaGateway;

    public void execute(Long id) {
        if (!pessoaGateway.existePorId(id)) {
            throw RecursoNaoEncontradoException.builder().mensagem("a pessoa com id " + id + " não foi encontrada")
                    .detalhes("").build();
        }
        pessoaGateway.deletarPorId(id);
    }
}
