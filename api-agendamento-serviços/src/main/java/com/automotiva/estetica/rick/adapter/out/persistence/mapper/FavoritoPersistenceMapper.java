package com.automotiva.estetica.rick.adapter.out.persistence.mapper;

import com.automotiva.estetica.rick.adapter.out.persistence.jpa.FavoritoJpaEntity;
import com.automotiva.estetica.rick.domain.entity.Favorito;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {PessoaPersistenceMapper.class, ServicoPersistenceMapper.class})
public interface FavoritoPersistenceMapper {

    Favorito toDomain(FavoritoJpaEntity entity);

    FavoritoJpaEntity toJpaEntity(Favorito domain);
}
