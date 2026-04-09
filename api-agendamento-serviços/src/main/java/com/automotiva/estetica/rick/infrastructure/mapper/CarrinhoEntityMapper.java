package com.automotiva.estetica.rick.infrastructure.mapper;

import com.automotiva.estetica.rick.infrastructure.entity.CarrinhoEntity;
import com.automotiva.estetica.rick.domain.entity.Carrinho;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PessoaEntityMapper.class, ServicoEntityMapper.class})
public interface CarrinhoEntityMapper {

    Carrinho toDomain(CarrinhoEntity entity);

    CarrinhoEntity toEntity(Carrinho domain);
}
