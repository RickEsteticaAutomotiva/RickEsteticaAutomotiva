package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.Servico;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServicoGateway {

    Servico salvar(Servico servico);

    Optional<Servico> buscarPorId(Long id);

    Page<Servico> buscarTodos(String filtro, Pageable pageable);

    boolean existePorId(Long id);

    void deletarPorId(Long id);
}
