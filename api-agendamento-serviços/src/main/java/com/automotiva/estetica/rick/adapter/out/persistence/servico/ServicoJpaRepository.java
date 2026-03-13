package com.automotiva.estetica.rick.adapter.out.persistence.servico;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ServicoJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
interface ServicoJpaRepository
        extends
            JpaRepository<ServicoJpaEntity, Long>,
            JpaSpecificationExecutor<ServicoJpaEntity> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = "categoria")
    Optional<ServicoJpaEntity> findById(@NonNull Long id);

    @Override
    @NonNull
    @EntityGraph(attributePaths = "categoria")
    Page<ServicoJpaEntity> findAll(Specification<ServicoJpaEntity> spec, @NonNull Pageable pageable);

    @EntityGraph(attributePaths = "categoria")
    List<ServicoJpaEntity> findByIdIn(List<Long> ids);
}
