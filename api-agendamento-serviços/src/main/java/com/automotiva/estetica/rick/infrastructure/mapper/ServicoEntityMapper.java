package com.automotiva.estetica.rick.infrastructure.mapper;

import com.automotiva.estetica.rick.infrastructure.entity.ServicoEntity;
import com.automotiva.estetica.rick.domain.entity.Servico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoriaEntityMapper.class})
public interface ServicoEntityMapper {

    Servico toDomain(ServicoEntity entity);

    @Mapping(target = "deletadoEm", ignore = true)
    ServicoEntity toEntity(Servico domain);
}
