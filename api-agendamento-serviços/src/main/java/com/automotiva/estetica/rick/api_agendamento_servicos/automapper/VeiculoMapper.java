package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.VeiculoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.VeiculoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VeiculoMapper {

    @Mapping(source = "pessoa.id", target = "idPessoa")
    VeiculoDto veiculoParaVeiculoDto(VeiculoEntity veiculo);

    @Mapping(target = "pessoa", ignore = true)
    VeiculoEntity veiculoDtoParaVeiculo(VeiculoDto veiculoDto);

    @Mapping(target = "pessoa", ignore = true)
    void atualizarVeiculoEntityFromDto(VeiculoDto dto, @MappingTarget VeiculoEntity entity);

    List<VeiculoDto> veiculosParaVeiculosDto(List<VeiculoEntity> veiculos);
}