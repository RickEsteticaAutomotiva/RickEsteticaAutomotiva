package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CarrinhoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.CarrinhoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarrinhoMapper {

    @Mapping(target = "pessoa.id", source = "idPessoa")
    @Mapping(target = "servico.id", source = "idServico")
    CarrinhoEntity carrinhoDtoParaEntity(CarrinhoDto dto);

    @Mapping(target = "idPessoa", source = "pessoa.id")
    @Mapping(target = "idServico", source = "servico.id")
    CarrinhoDto entityParaCarrinhoDto(CarrinhoEntity entity);

    List<CarrinhoDto> carrinhosParaDtos(List<CarrinhoEntity> entities);
}