package com.automotiva.estetica.rick.adapter.out.persistence.mapper;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ServicoJpaEntity;
import com.automotiva.estetica.rick.domain.entity.Servico;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {CategoriaPersistenceMapper.class})
public interface ServicoPersistenceMapper {

    Servico toDomain(ServicoJpaEntity entity);

    ServicoJpaEntity toJpaEntity(Servico domain);
}
