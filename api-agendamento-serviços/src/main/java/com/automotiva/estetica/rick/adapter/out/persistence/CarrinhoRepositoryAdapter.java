package com.automotiva.estetica.rick.adapter.out.persistence;

import com.automotiva.estetica.rick.adapter.out.persistence.jpa.PessoaJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpa.ServicoJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.CarrinhoPersistenceMapper;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.PessoaPersistenceMapper;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.ServicoPersistenceMapper;
import com.automotiva.estetica.rick.application.port.out.CarrinhoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Carrinho;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CarrinhoRepositoryAdapter implements CarrinhoRepositoryPort {

    private final CarrinhoJpaRepository jpaRepository;
    private final CarrinhoPersistenceMapper mapper;
    private final PessoaPersistenceMapper pessoaMapper;
    private final ServicoPersistenceMapper servicoMapper;

    @Override
    public Carrinho salvar(Carrinho carrinho) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(carrinho)));
    }

    @Override
    public Optional<Carrinho> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Carrinho> buscarPorPessoaId(Long pessoaId) {
        return jpaRepository.findByPessoaId(pessoaId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existePorPessoaEServico(Pessoa pessoa, Servico servico) {
        PessoaJpaEntity pessoaJpa = pessoaMapper.toJpaEntity(pessoa);
        ServicoJpaEntity servicoJpa = servicoMapper.toJpaEntity(servico);
        return jpaRepository.existsByPessoaAndServico(pessoaJpa, servicoJpa);
    }

    @Override
    public void deletarPorId(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deletarTodos(List<Carrinho> itens) {
        jpaRepository.deleteAll(itens.stream().map(mapper::toJpaEntity).toList());
    }
}
