package com.automotiva.estetica.rick.infrastructure.mapper;

import com.automotiva.estetica.rick.infrastructure.entity.ItemServicoEntity;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ServicoEntityMapper.class, OrdemServicoEntityMapper.class})
public interface ItemServicoEntityMapper {

    ItemServico toDomain(ItemServicoEntity entity);

    ItemServicoEntity toEntity(ItemServico domain);
}
