package com.automotiva.estetica.rick.application.mapper;

import com.automotiva.estetica.rick.application.dto.request.ServicoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoResponse;
import com.automotiva.estetica.rick.domain.entity.Servico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServicoDTOMapper {

    @Mapping(target = "categoria.id", source = "categoriaId")
    Servico toDomain(ServicoRequest request);

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNome", source = "categoria.nome")
    ServicoResponse toResponse(Servico servico);
}
