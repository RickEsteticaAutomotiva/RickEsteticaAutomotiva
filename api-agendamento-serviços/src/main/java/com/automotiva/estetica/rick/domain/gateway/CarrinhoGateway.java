package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.Carrinho;
import java.util.List;
import java.util.Optional;

public interface CarrinhoGateway {

    Carrinho salvar(Carrinho carrinho);

    Optional<Carrinho> buscarPorId(Long id);

    List<Carrinho> buscarPorPessoaId(Long pessoaId);

    boolean existePorPessoaEServico(Long pessoaId, Long servicoId);

    void deletarPorId(Long id);

    void deletarTodos(List<Carrinho> itens);
}
