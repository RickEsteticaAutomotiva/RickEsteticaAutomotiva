package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ItemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ItemServicoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemServicoMapper {

    ItemServicoMapper INSTANCE = Mappers.getMapper(ItemServicoMapper.class);

    @Mapping(source = "servico.id", target = "idServico")
    @Mapping(source = "ordemServico.id", target = "idOrdemServico")
    ItemServicoDto itemServicoParaItemServicoDto(ItemServicoEntity entity);

    @Mapping(target = "servico", ignore = true)
    @Mapping(target = "ordemServico", ignore = true)
    ItemServicoEntity itemServicoDtoParaItemServico(ItemServicoDto dto);

    @Mapping(target = "servico", ignore = true)
    @Mapping(target = "ordemServico", ignore = true)
    void atualizarItemServicoEntityFromDto(ItemServicoDto dto, @MappingTarget ItemServicoEntity entity);

    List<ItemServicoDto> itemServicosParaItemServicosDto(List<ItemServicoEntity> entities);
}
