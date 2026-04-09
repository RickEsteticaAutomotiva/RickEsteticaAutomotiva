package com.automotiva.estetica.rick.infrastructure.mapper;

import com.automotiva.estetica.rick.infrastructure.entity.MotivoCancelamentoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.StatusEntity;
import com.automotiva.estetica.rick.domain.entity.MotivoCancelamento;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Status;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {VeiculoEntityMapper.class})
public interface OrdemServicoEntityMapper {

    OrdemServico toDomain(OrdemServicoEntity entity);

    OrdemServicoEntity toEntity(OrdemServico domain);

    default Status toDomain(StatusEntity entity) {
        if (entity == null)
            return null;
        return Status.builder().id(entity.getId()).descricao(entity.getDescricao()).build();
    }

    default StatusEntity toEntity(Status domain) {
        if (domain == null)
            return null;
        return StatusEntity.builder().id(domain.getId()).descricao(domain.getDescricao()).build();
    }

    default MotivoCancelamento toDomain(MotivoCancelamentoEntity entity) {
        if (entity == null)
            return null;
        return MotivoCancelamento.builder().id(entity.getId()).descricao(entity.getDescricao()).build();
    }

    default MotivoCancelamentoEntity toEntity(MotivoCancelamento domain) {
        if (domain == null)
            return null;
        return MotivoCancelamentoEntity.builder().id(domain.getId()).descricao(domain.getDescricao()).build();
    }
}
