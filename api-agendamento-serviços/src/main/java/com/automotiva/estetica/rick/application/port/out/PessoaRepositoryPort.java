package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PessoaRepositoryPort {

    Pessoa salvar(Pessoa pessoa);

    Optional<Pessoa> buscarPorId(Long id);

    Optional<Pessoa> buscarPorEmail(String email);

    Page<Pessoa> buscarTodos(String filtro, Pageable pageable);

    boolean existePorCpf(String cpf);

    boolean existePorEmail(String email);

    boolean existePorId(Long id);

    void deletarPorId(Long id);
}
