package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.domain.entity.Favorito;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import java.util.List;
import java.util.Optional;

public interface FavoritoRepositoryPort {

    Favorito salvar(Favorito favorito);

    Optional<Favorito> buscarPorId(Long id);

    List<Favorito> buscarPorPessoaId(Long pessoaId);

    boolean existePorPessoaEServico(Pessoa pessoa, Servico servico);

    void deletarPorId(Long id);
}
