package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.Favorito;
import java.util.List;
import java.util.Optional;

public interface FavoritoGateway {

    Favorito salvar(Favorito favorito);

    Optional<Favorito> buscarPorId(Long id);

    List<Favorito> buscarPorPessoaId(Long pessoaId);

    boolean existePorPessoaEServico(Long pessoaId, Long servicoId);

    void deletarPorId(Long id);
}
