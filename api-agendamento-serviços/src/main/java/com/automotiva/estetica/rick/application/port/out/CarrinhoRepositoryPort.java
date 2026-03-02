package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.domain.entity.Carrinho;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import java.util.List;
import java.util.Optional;

public interface CarrinhoRepositoryPort {

    Carrinho salvar(Carrinho carrinho);

    Optional<Carrinho> buscarPorId(Long id);

    List<Carrinho> buscarPorPessoaId(Long pessoaId);

    boolean existePorPessoaEServico(Pessoa pessoa, Servico servico);

    void deletarPorId(Long id);

    void deletarTodos(List<Carrinho> itens);
}
