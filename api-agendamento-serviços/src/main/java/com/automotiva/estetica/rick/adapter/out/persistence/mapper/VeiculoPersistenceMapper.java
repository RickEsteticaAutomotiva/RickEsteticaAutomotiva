package com.automotiva.estetica.rick.adapter.out.persistence.mapper;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.VeiculoJpaEntity;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PessoaPersistenceMapper.class})
public interface VeiculoPersistenceMapper {

    Veiculo toDomain(VeiculoJpaEntity entity);

    @Mapping(target = "deletadoEm", ignore = true)
    VeiculoJpaEntity toJpaEntity(Veiculo domain);
}
