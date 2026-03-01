package com.automotiva.estetica.rick.adapter.out.persistence.mapper;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.CarrinhoJpaEntity;
import com.automotiva.estetica.rick.domain.entity.Carrinho;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {PessoaPersistenceMapper.class, ServicoPersistenceMapper.class})
public interface CarrinhoPersistenceMapper {

    Carrinho toDomain(CarrinhoJpaEntity entity);

    CarrinhoJpaEntity toJpaEntity(Carrinho domain);
}
