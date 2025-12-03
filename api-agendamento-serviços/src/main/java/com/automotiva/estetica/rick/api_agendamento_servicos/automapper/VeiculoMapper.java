package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.VeiculoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.VeiculoEntity;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VeiculoMapper {

    @Mapping(source = "pessoa.id", target = "idPessoa")
    VeiculoDto veiculoParaVeiculoDto(VeiculoEntity veiculo);

    VeiculoEntity veiculoDtoParaVeiculo(VeiculoDto veiculoDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void atualizarVeiculoEntityFromDto(VeiculoDto dto, @MappingTarget VeiculoEntity entity);

    List<VeiculoDto> veiculosParaVeiculosDto(List<VeiculoEntity> veiculos);
}
