package com.automotiva.estetica.rick.application.mapper;

import com.automotiva.estetica.rick.application.dto.request.VeiculoRequest;
import com.automotiva.estetica.rick.application.dto.response.VeiculoResponse;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VeiculoDTOMapper {

    @Mapping(target = "pessoa.id", source = "idPessoa")
    Veiculo toDomain(VeiculoRequest request);

    @Mapping(target = "idPessoa", source = "pessoa.id")
    VeiculoResponse toResponse(Veiculo veiculo);
}
