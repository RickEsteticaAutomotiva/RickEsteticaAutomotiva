package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CategoriaDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.CategoriaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    CategoriaDto categoriaEntiryParaCategoriaDto(CategoriaEntity categoria);

    CategoriaEntity categoriaDtoParaCategoriaEntity(CategoriaDto categoriaDto);
}
