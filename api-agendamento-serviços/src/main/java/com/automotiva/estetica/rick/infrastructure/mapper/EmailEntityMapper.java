package com.automotiva.estetica.rick.infrastructure.mapper;

import com.automotiva.estetica.rick.infrastructure.entity.EmailEntity;
import com.automotiva.estetica.rick.domain.entity.Email;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PessoaEntityMapper.class})
public interface EmailEntityMapper {

    Email toDomain(EmailEntity entity);

    EmailEntity toEntity(Email domain);
}
