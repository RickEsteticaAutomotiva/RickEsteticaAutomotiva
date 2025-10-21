package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.FavoritoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoFavoritoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.FavoritoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoritoMapper {

    @Mapping(target = "pessoa.id", source = "idPessoa")
    @Mapping(target = "servico.id", source = "idServico")
    FavoritoEntity favoritoDtoParaEntity(FavoritoDto dto);

    @Mapping(target = "idPessoa", source = "pessoa.id")
    @Mapping(target = "idServico", source = "servico.id")
    FavoritoDto entityParaFavoritoDto(FavoritoEntity entity);

    @Mapping(target = "idServico", source = "id")
    ServicoFavoritoDto servicoParaServicoFavoritoDto(ServicoEntity entity);
}
