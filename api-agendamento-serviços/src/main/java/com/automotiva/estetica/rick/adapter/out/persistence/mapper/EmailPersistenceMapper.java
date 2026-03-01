package com.automotiva.estetica.rick.adapter.out.persistence.mapper;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.EmailJpaEntity;
import com.automotiva.estetica.rick.domain.entity.Email;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {PessoaPersistenceMapper.class})
public interface EmailPersistenceMapper {

    Email toDomain(EmailJpaEntity entity);

    EmailJpaEntity toJpaEntity(Email domain);
}
