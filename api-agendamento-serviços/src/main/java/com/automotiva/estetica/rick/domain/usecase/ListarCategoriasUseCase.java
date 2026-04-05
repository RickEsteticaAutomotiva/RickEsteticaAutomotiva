package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.gateway.CategoriaGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use Case para listar todas as Categorias.
 */
@Service
@RequiredArgsConstructor
public class ListarCategoriasUseCase {

    private final CategoriaGateway categoriaGateway;

    public List<Categoria> execute() {
        return categoriaGateway.buscarTodas();
    }
}
