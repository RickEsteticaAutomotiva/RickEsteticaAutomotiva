package com.automotiva.estetica.rick.adapter.out.persistence.mapper;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.PessoaJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.RoleJpaEntity;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper MapStruct para conversão entre {@link PessoaJpaEntity} e
 * {@link Pessoa}.
 *
 * <p>
 * O campo {@code roles} precisa de conversão explícita porque o tipo no domínio
 * é {@code
 * Set<RoleEnum>} enquanto na entidade JPA é {@code Set<RoleJpaEntity>}.
 *
 * <p>
 * Camada: adapter/out/persistence/mapper.
 */
@Mapper(componentModel = "spring")
public interface PessoaPersistenceMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "roleEntitiesToEnums")
    Pessoa toDomain(PessoaJpaEntity entity);

    /**
     * Converte domínio → entidade JPA.
     *
     * <p>
     * O campo {@code roles} é ignorado aqui: a associação ManyToMany com
     * {@link RoleJpaEntity} é resolvida no {@code PessoaRepositoryAdapter}, que
     * busca as entidades reais pelo {@code
     * RoleJpaRepository} antes de salvar. Isso evita a criação de registros
     * duplicados na tabela {@code role}.
     */
    @Mapping(target = "roles", ignore = true)
    PessoaJpaEntity toJpaEntity(Pessoa domain);

    // ─── Helpers de conversão ────────────────────────────────────────────────

    /**
     * Converte {@code Set<RoleJpaEntity>} → {@code Set<RoleEnum>} para o domínio.
     * Retorna conjunto vazio quando a entrada for nula.
     */
    @Named("roleEntitiesToEnums")
    default Set<RoleEnum> roleEntitiesToEnums(Set<RoleJpaEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptySet();
        }
        return entities.stream().map(RoleJpaEntity::getNome).collect(Collectors.toSet());
    }
}
