package com.automotiva.estetica.rick.adapter.out.persistence.mapper;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.MotivoCancelamentoJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.OrdemServicoJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.StatusJpaEntity;
import com.automotiva.estetica.rick.domain.entity.MotivoCancelamento;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Status;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {VeiculoPersistenceMapper.class})
public interface OrdemServicoPersistenceMapper {

    OrdemServico toDomain(OrdemServicoJpaEntity entity);

    OrdemServicoJpaEntity toJpaEntity(OrdemServico domain);

    default Status toDomain(StatusJpaEntity entity) {
        if (entity == null)
            return null;
        return Status.builder().id(entity.getId()).descricao(entity.getDescricao()).build();
    }

    default StatusJpaEntity toJpaEntity(Status domain) {
        if (domain == null)
            return null;
        return StatusJpaEntity.builder().id(domain.getId()).descricao(domain.getDescricao()).build();
    }

    default MotivoCancelamento toDomain(MotivoCancelamentoJpaEntity entity) {
        if (entity == null)
            return null;
        return MotivoCancelamento.builder().id(entity.getId()).descricao(entity.getDescricao()).build();
    }

    default MotivoCancelamentoJpaEntity toJpaEntity(MotivoCancelamento domain) {
        if (domain == null)
            return null;
        return MotivoCancelamentoJpaEntity.builder().id(domain.getId()).descricao(domain.getDescricao()).build();
    }
}
