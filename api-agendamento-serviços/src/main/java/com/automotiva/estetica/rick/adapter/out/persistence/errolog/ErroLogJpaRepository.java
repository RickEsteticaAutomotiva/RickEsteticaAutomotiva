package com.automotiva.estetica.rick.adapter.out.persistence.errolog;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ErroLogJpaEntity;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
interface ErroLogJpaRepository
        extends
            JpaRepository<ErroLogJpaEntity, Long>,
            JpaSpecificationExecutor<ErroLogJpaEntity> {

    @Modifying
    @Transactional
    @Query("DELETE FROM ErroLogJpaEntity e WHERE e.timestamp < :dataLimite")
    void deleteByTimestampBefore(@Param("dataLimite") LocalDateTime dataLimite);
}
