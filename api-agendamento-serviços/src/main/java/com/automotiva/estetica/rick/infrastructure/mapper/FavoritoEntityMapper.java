package com.automotiva.estetica.rick.infrastructure.mapper;

import com.automotiva.estetica.rick.infrastructure.entity.FavoritoEntity;
import com.automotiva.estetica.rick.domain.entity.Favorito;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PessoaEntityMapper.class, ServicoEntityMapper.class})
public interface FavoritoEntityMapper {

    Favorito toDomain(FavoritoEntity entity);

    FavoritoEntity toEntity(Favorito domain);
}
