package com.automotiva.estetica.rick.adapter.out.persistence.categoria;

import com.automotiva.estetica.rick.adapter.out.persistence.mapper.CategoriaPersistenceMapper;
import com.automotiva.estetica.rick.application.port.out.CategoriaRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final CategoriaJpaRepository jpaRepository;
    private final CategoriaPersistenceMapper mapper;

    @Override
    public Categoria salvar(Categoria categoria) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(categoria)));
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Categoria> buscarTodas() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return jpaRepository.existsById(id);
    }
}
