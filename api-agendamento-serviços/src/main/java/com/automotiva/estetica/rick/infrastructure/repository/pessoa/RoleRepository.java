package com.automotiva.estetica.rick.infrastructure.repository.pessoa;

import com.automotiva.estetica.rick.infrastructure.entity.RoleEntity;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * RepositÃ³rio JPA para a entidade {@link RoleEntity}.
 *
 * <p>
 * Utilizado pela implementaÃ§Ã£o de gateway de pessoa para resolver roles no
 * momento da persistÃªncia.
 *
 * <p>
 * Camada: infrastructure/persistence.
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    /**
     * Busca uma role pelo seu enum, ex.: {@code findByNome(RoleEnum.ROLE_USER)}.
     * Retorna {@link Optional#empty()} se a role ainda nÃ£o existir no banco.
     */
    Optional<RoleEntity> findByNome(RoleEnum nome);
}
