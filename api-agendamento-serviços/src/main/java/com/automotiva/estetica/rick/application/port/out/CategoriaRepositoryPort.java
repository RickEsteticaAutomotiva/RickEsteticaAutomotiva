package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.domain.entity.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepositoryPort {

    Categoria salvar(Categoria categoria);

    Optional<Categoria> buscarPorId(Long id);

    List<Categoria> buscarTodas();

    boolean existePorId(Long id);
}
