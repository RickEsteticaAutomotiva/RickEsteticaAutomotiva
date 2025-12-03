package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CarrinhoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoCarrinhoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.CarrinhoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarrinhoMapper {

    @Mapping(target = "pessoa.id", source = "idPessoa")
    @Mapping(target = "servico.id", source = "idServico")
    CarrinhoEntity carrinhoDtoParaEntity(CarrinhoDto dto);

    @Mapping(target = "idServico", source = "id")
    ServicoCarrinhoDto servicoParaServicoCarrinhoDto(ServicoEntity entity);
}
