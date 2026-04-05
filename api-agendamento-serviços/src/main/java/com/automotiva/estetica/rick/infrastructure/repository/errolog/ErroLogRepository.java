package com.automotiva.estetica.rick.infrastructure.repository.errolog;

import com.automotiva.estetica.rick.infrastructure.entity.ErroLogEntity;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ErroLogRepository
        extends
            JpaRepository<ErroLogEntity, Long>,
            JpaSpecificationExecutor<ErroLogEntity> {

    @Modifying
    @Transactional
    @Query("DELETE FROM ErroLogEntity e WHERE e.timestamp < :dataLimite")
    void deleteByTimestampBefore(@Param("dataLimite") LocalDateTime dataLimite);
}
