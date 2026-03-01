package com.automotiva.estetica.rick.adapter.out.persistence.mapper;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.CategoriaJpaEntity;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaPersistenceMapper {

    Categoria toDomain(CategoriaJpaEntity entity);

    CategoriaJpaEntity toJpaEntity(Categoria domain);
}
