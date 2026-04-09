package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Use Case para listar Pessoas paginadas.
 */
@Service
@RequiredArgsConstructor
public class ListarPessoasUseCase {

    private final PessoaGateway pessoaGateway;

    public Page<Pessoa> execute(String filtro, Pageable pageable) {
        return pessoaGateway.buscarTodos(filtro, pageable);
    }
}
