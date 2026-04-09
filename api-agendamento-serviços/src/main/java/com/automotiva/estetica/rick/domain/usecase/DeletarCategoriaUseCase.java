package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.CategoriaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use Case para deletar uma Categoria.
 */
@Service
@RequiredArgsConstructor
public class DeletarCategoriaUseCase {

    private final CategoriaGateway categoriaGateway;

    public void execute(Long id) {
        if (!categoriaGateway.existePorId(id)) {
            throw RecursoNaoEncontradoException.builder().mensagem("Categoria com id " + id + " não foi encontrada")
                    .detalhes("").build();
        }
        categoriaGateway.deletarPorId(id);
    }
}
