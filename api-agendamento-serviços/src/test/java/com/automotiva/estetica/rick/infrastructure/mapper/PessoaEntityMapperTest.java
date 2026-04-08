package com.automotiva.estetica.rick.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import com.automotiva.estetica.rick.infrastructure.entity.RoleEntity;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@DisplayName("Testes de PessoaEntityMapper")
class PessoaEntityMapperTest {

    private final PessoaEntityMapper mapper = Mappers.getMapper(PessoaEntityMapper.class);

    @Test
    @DisplayName("roleEntitiesToEnums deve retornar vazio quando entrada for nula")
    void roleEntitiesToEnums_quandoNulo_deveRetornarVazio() {
        Set<RoleEnum> resultado = mapper.roleEntitiesToEnums(null);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("roleEntitiesToEnums deve mapear nomes de roles")
    void roleEntitiesToEnums_quandoPreenchido_deveMapear() {
        Set<RoleEnum> resultado = mapper
                .roleEntitiesToEnums(Set.of(RoleEntity.builder().nome(RoleEnum.ROLE_CLIENTE).build()));

        assertEquals(1, resultado.size());
        assertTrue(resultado.contains(RoleEnum.ROLE_CLIENTE));
    }
}
