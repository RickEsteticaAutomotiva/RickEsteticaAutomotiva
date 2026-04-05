package com.automotiva.estetica.rick.infrastructure.mapper;

import com.automotiva.estetica.rick.infrastructure.entity.CategoriaEntity;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaEntityMapper {

    Categoria toDomain(CategoriaEntity entity);

    CategoriaEntity toEntity(Categoria domain);
}
