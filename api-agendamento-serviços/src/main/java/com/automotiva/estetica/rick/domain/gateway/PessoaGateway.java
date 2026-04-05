package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Gateway que abstrai a persistência de Pessoa. Inverte a dependência: domain
 * depende de uma interface, infraestrutura implementa.
 */
public interface PessoaGateway {

    Pessoa salvar(Pessoa pessoa);

    Optional<Pessoa> buscarPorId(Long id);

    Optional<Pessoa> buscarPorEmail(String email);

    Page<Pessoa> buscarTodos(String filtro, Pageable pageable);

    boolean existePorCpf(String cpf);

    boolean existePorEmail(String email);

    boolean existePorId(Long id);

    void deletarPorId(Long id);
}
