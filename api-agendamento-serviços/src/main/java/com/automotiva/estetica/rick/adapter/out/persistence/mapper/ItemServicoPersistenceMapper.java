package com.automotiva.estetica.rick.adapter.out.persistence.mapper;

import com.automotiva.estetica.rick.adapter.out.persistence.jpa.ItemServicoJpaEntity;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {ServicoPersistenceMapper.class, OrdemServicoPersistenceMapper.class})
public interface ItemServicoPersistenceMapper {

    ItemServico toDomain(ItemServicoJpaEntity entity);

    ItemServicoJpaEntity toJpaEntity(ItemServico domain);
}
