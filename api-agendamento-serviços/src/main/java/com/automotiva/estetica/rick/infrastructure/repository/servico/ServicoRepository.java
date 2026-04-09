package com.automotiva.estetica.rick.infrastructure.repository.servico;

import com.automotiva.estetica.rick.infrastructure.entity.ServicoEntity;
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
public interface ServicoRepository extends JpaRepository<ServicoEntity, Long>, JpaSpecificationExecutor<ServicoEntity> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = "categoria")
    Optional<ServicoEntity> findById(@NonNull Long id);

    @Override
    @NonNull
    @EntityGraph(attributePaths = "categoria")
    Page<ServicoEntity> findAll(Specification<ServicoEntity> spec, @NonNull Pageable pageable);

    @EntityGraph(attributePaths = "categoria")
    List<ServicoEntity> findByIdIn(List<Long> ids);
}
