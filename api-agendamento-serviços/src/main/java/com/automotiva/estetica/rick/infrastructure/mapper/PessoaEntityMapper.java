package com.automotiva.estetica.rick.infrastructure.mapper;

import com.automotiva.estetica.rick.infrastructure.entity.PessoaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.RoleEntity;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper MapStruct para conversÃ£o entre {@link PessoaEntity} e {@link Pessoa}.
 *
 * <p>
 * O campo {@code roles} precisa de conversÃ£o explÃ­cita porque o tipo no
 * domÃ­nio Ã© {@code
 * Set<RoleEnum>} enquanto na entidade JPA Ã© {@code Set<RoleEntity>}.
 *
 * <p>
 * Camada: infrastructure/persistence/mapper.
 */
@Mapper(componentModel = "spring")
public interface PessoaEntityMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "roleEntitiesToEnums")
    Pessoa toDomain(PessoaEntity entity);

    /**
     * Converte domÃ­nio â†’ entidade JPA.
     *
     * <p>
     * O campo {@code roles} Ã© ignorado aqui: a associaÃ§Ã£o ManyToMany com
     * {@link RoleEntity} Ã© resolvida no {@code PessoaGatewayImpl}, que busca as
     * entidades reais pelo {@code
     * RoleRepository} antes de salvar. Isso evita a criaÃ§Ã£o de registros
     * duplicados na tabela {@code role}.
     *
     * <p>
     * O campo {@code deletadoEm} Ã© ignorado porque soft delete Ã© responsabilidade
     * da infraestrutura de persistÃªncia, nÃ£o do domÃ­nio.
     */
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "deletadoEm", ignore = true)
    PessoaEntity toEntity(Pessoa domain);

    // â”€â”€â”€ Helpers de conversÃ£o
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Converte {@code Set<RoleEntity>} â†’ {@code Set<RoleEnum>} para o domÃ­nio.
     * Retorna conjunto vazio quando a entrada for nula.
     */
    @Named("roleEntitiesToEnums")
    default Set<RoleEnum> roleEntitiesToEnums(Set<RoleEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptySet();
        }
        return entities.stream().map(RoleEntity::getNome).collect(Collectors.toSet());
    }
}
