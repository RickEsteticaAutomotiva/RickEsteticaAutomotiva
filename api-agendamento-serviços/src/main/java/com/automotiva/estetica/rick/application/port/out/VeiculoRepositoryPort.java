package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.domain.entity.Veiculo;
import java.util.List;
import java.util.Optional;

public interface VeiculoRepositoryPort {

    Veiculo salvar(Veiculo veiculo);

    Optional<Veiculo> buscarPorId(Long id);

    List<Veiculo> buscarTodos();

    List<Veiculo> buscarPorPessoaId(Long pessoaId);

    boolean existePorId(Long id);

    void deletarPorId(Long id);
}
