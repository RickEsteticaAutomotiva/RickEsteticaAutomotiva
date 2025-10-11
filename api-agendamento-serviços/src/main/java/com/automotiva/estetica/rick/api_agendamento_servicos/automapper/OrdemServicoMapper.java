package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdemServicoMapper {

    OrdemServicoMapper INSTANCE = Mappers.getMapper(OrdemServicoMapper.class);

    OrdemServicoDto ordemServicoParaOrdemServicoDto(OrdemServicoEntity entity);

    OrdemServicoEntity ordemServicoDtoParaOrdemServico(OrdemServicoDto dto);

    void atualizarOrdemServicoEntityFromDto(OrdemServicoDto dto, @MappingTarget OrdemServicoEntity entity);

    List<OrdemServicoDto> ordemServicosParaOrdemServicosDto(List<OrdemServicoEntity> entities);
}
