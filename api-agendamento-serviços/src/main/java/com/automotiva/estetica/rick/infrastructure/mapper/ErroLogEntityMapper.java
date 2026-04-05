package com.automotiva.estetica.rick.infrastructure.mapper;

import com.automotiva.estetica.rick.infrastructure.entity.ErroLogEntity;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import org.mapstruct.Mapper;

/** Mapper MapStruct entre domÃ­nio ErroLog e sua entidade JPA. */
@Mapper(componentModel = "spring")
public interface ErroLogEntityMapper {

    ErroLog toDomain(ErroLogEntity entity);

    ErroLogEntity toEntity(ErroLog domain);
}
