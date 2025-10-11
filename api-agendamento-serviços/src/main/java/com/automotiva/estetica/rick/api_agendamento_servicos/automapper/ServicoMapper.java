package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServicoMapper {

    ServicoMapper INSTANCE = Mappers.getMapper(ServicoMapper.class);

    ServicoDto servicoParaServicoDto(ServicoEntity entity);

    ServicoEntity servicoDtoParaServico(ServicoDto dto);

    void atualizarServicoEntityFromDto(ServicoDto dto, @MappingTarget ServicoEntity entity);

    List<ServicoDto> servicosParaServicosDto(List<ServicoEntity> entities);
}
