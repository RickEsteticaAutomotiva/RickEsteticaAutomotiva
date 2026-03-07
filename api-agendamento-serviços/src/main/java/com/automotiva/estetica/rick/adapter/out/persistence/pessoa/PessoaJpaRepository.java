package com.automotiva.estetica.rick.adapter.out.persistence.pessoa;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.PessoaJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
interface PessoaJpaRepository
        extends JpaRepository<PessoaJpaEntity, Long>, JpaSpecificationExecutor<PessoaJpaEntity> {

    Optional<PessoaJpaEntity> findByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}
