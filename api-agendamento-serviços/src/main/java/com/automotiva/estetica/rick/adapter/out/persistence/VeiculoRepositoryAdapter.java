package com.automotiva.estetica.rick.adapter.out.persistence;

import com.automotiva.estetica.rick.adapter.out.persistence.mapper.VeiculoPersistenceMapper;
import com.automotiva.estetica.rick.application.port.out.VeiculoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VeiculoRepositoryAdapter implements VeiculoRepositoryPort {

    private final VeiculoJpaRepository jpaRepository;
    private final VeiculoPersistenceMapper mapper;

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(veiculo)));
    }

    @Override
    public Optional<Veiculo> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Veiculo> buscarTodos() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Veiculo> buscarPorPessoaId(Long pessoaId) {
        return jpaRepository.findByPessoa_Id(pessoaId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deletarPorId(Long id) {
        jpaRepository.deleteById(id);
    }
}
