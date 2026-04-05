package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.CategoriaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use Case para buscar Categoria por ID.
 */
@Service
@RequiredArgsConstructor
public class BuscarCategoriaPorIdUseCase {

    private final CategoriaGateway categoriaGateway;

    public Categoria execute(Long id) {
        return categoriaGateway.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("Categoria com id " + id + " não foi encontrada").detalhes("").build());
    }
}
