package com.automotiva.estetica.rick.adapter.out.persistence.pessoa;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.RoleJpaEntity;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade {@link RoleJpaEntity}.
 *
 * <p>
 * Package-private: utilizado apenas pelo {@code PessoaRepositoryAdapter} dentro
 * do mesmo pacote, sem exposição para as camadas superiores.
 *
 * <p>
 * Camada: adapter/out/persistence.
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Long> {

    /**
     * Busca uma role pelo seu enum, ex.: {@code findByNome(RoleEnum.ROLE_USER)}.
     * Retorna {@link Optional#empty()} se a role ainda não existir no banco.
     */
    Optional<RoleJpaEntity> findByNome(RoleEnum nome);
}
