package com.automotiva.estetica.rick.adapter.out.persistence.mapper;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ErroLogJpaEntity;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import org.mapstruct.Mapper;

/** Mapper MapStruct entre domínio ErroLog e sua entidade JPA. */
@Mapper(componentModel = "spring")
public interface ErroLogPersistenceMapper {

    ErroLog toDomain(ErroLogJpaEntity entity);

    ErroLogJpaEntity toJpaEntity(ErroLog domain);
}
