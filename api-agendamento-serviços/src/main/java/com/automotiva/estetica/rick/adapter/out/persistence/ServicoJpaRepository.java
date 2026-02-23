package com.automotiva.estetica.rick.adapter.out.persistence;

import com.automotiva.estetica.rick.adapter.out.persistence.jpa.ServicoJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
interface ServicoJpaRepository
        extends JpaRepository<ServicoJpaEntity, Long>, JpaSpecificationExecutor<ServicoJpaEntity> {

    List<ServicoJpaEntity> findByIdIn(List<Long> ids);
}
