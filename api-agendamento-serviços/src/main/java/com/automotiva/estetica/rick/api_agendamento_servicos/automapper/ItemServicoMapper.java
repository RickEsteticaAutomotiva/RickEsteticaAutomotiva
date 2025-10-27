package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ItemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ItemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ItemServicoMapper {

    ItemServicoMapper INSTANCE = Mappers.getMapper(ItemServicoMapper.class);

    // Removido mapeamento para servico.id e ordemServico.id
    @Mapping(source = "servico.id", target = "idServico")
    @Mapping(source = "ordemServico.id", target = "idOrdemServico")
    ItemServicoDto itemServicoParaItemServicoDto(ItemServicoEntity entity);

    // Removido ignore para ordemServico se não existir
    @Mapping(target = "servico", ignore = true)
    ItemServicoEntity itemServicoDtoParaItemServico(ItemServicoDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void atualizarItemServicoEntityFromDto(ItemServicoDto dto, @MappingTarget ItemServicoEntity entity);

    List<ItemServicoDto> itemServicosParaItemServicosDto(List<ItemServicoEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "servicoId", target = "servico")
    @Mapping(target = "ordemServico", expression = "java(mapOrdemServico(entity.getId()))")
    @Mapping(source = "entityServico.preco", target = "preco")
    ItemServicoEntity ordemServicoParaItemServicoEntity(Long servicoId, OrdemServicoEntity entity, ServicoEntity entityServico);

    default ServicoEntity mapServico(Long id) {
        if (id == null) return null;
        ServicoEntity servico = new ServicoEntity();
        servico.setId(id);
        return servico;
    }

    default OrdemServicoEntity mapOrdemServico(Long id) {
        if (id == null) return null;
        OrdemServicoEntity ordemServico = new OrdemServicoEntity();
        ordemServico.setId(id);
        return ordemServico;
    }
}
