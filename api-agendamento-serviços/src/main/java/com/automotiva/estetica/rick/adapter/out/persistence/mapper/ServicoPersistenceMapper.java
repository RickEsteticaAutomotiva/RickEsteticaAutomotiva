package com.automotiva.estetica.rick.adapter.out.persistence.mapper;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ServicoJpaEntity;
import com.automotiva.estetica.rick.domain.entity.Servico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoriaPersistenceMapper.class})
public interface ServicoPersistenceMapper {

    @Mapping(source = "duracaoMinutos", target = "duracaoMinutos")
    Servico toDomain(ServicoJpaEntity entity);

    @Mapping(target = "deletadoEm", ignore = true)
    ServicoJpaEntity toJpaEntity(Servico domain);
}
