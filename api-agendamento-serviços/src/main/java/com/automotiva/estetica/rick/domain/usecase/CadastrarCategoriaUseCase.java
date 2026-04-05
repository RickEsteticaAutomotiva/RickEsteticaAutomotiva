package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.gateway.CategoriaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use Case para cadastrar uma nova Categoria. Encapsula a lógica de negócio de
 * criação.
 */
@Service
@RequiredArgsConstructor
public class CadastrarCategoriaUseCase {

    private final CategoriaGateway categoriaGateway;

    public Categoria execute(Categoria categoria) {
        return categoriaGateway.salvar(categoria);
    }
}
