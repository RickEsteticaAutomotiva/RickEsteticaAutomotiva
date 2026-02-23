package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.domain.entity.Servico;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServicoRepositoryPort {

    Servico salvar(Servico servico);

    Optional<Servico> buscarPorId(Long id);

    Page<Servico> buscarTodos(String filtro, Pageable pageable);

    List<Servico> buscarPorIds(List<Long> ids);

    boolean existePorId(Long id);

    void deletarPorId(Long id);
}
