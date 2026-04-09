package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.Categoria;
import java.util.List;
import java.util.Optional;

/**
 * Gateway que abstrai a persistência de Categoria. Inverte a dependência:
 * domain depende de uma interface, infraestrutura implementa.
 */
public interface CategoriaGateway {

    Categoria salvar(Categoria categoria);

    Optional<Categoria> buscarPorId(Long id);

    List<Categoria> buscarTodas();

    boolean existePorId(Long id);

    void deletarPorId(Long id);
}
