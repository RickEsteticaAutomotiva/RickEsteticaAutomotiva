package com.automotiva.estetica.rick.infrastructure.mapper;

import com.automotiva.estetica.rick.infrastructure.entity.VeiculoEntity;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PessoaEntityMapper.class})
public interface VeiculoEntityMapper {

    Veiculo toDomain(VeiculoEntity entity);

    @Mapping(target = "deletadoEm", ignore = true)
    VeiculoEntity toEntity(Veiculo domain);
}
