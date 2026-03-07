package com.automotiva.estetica.rick.adapter.out.persistence.jpaentity;

import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entidade JPA que representa uma role de autorização.
 *
 * <p>Persistida na tabela {@code role}. A relação com {@code PessoaJpaEntity} é N:N gerenciada pela
 * tabela de junção {@code pessoa_roles}.
 *
 * <p>Camada: adapter/out/persistence/jpa (sem dependência das camadas internas).
 *
 * <p>Fonte: Spring Security Reference — Granting Authority, Baeldung "Spring Security — Roles and
 * Privileges".
 */
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class RoleJpaEntity extends BaseJpaEntity<Long> {

    /**
     * Nome da role armazenado como String (ex.: {@code "ROLE_ADMIN"}). Único para evitar duplicatas
     * na tabela de referência.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "nome", length = 30, nullable = false, unique = true)
    private RoleEnum nome;
}
