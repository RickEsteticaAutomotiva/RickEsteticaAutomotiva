package com.automotiva.estetica.rick.adapter.out.persistence.favorito;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.PessoaJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ServicoJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.FavoritoPersistenceMapper;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.PessoaPersistenceMapper;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.ServicoPersistenceMapper;
import com.automotiva.estetica.rick.application.port.out.FavoritoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Favorito;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FavoritoRepositoryAdapter implements FavoritoRepositoryPort {

    private final FavoritoJpaRepository jpaRepository;
    private final FavoritoPersistenceMapper mapper;
    private final PessoaPersistenceMapper pessoaMapper;
    private final ServicoPersistenceMapper servicoMapper;

    @Override
    public Favorito salvar(Favorito favorito) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(favorito)));
    }

    @Override
    public Optional<Favorito> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Favorito> buscarPorPessoaId(Long pessoaId) {
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
}
