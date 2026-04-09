package com.automotiva.estetica.rick.infrastructure.entity;

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
 * Entidade JPA que representa uma role de autorizaÃ§Ã£o.
 *
 * <p>
 * Persistida na tabela {@code role}. A relaÃ§Ã£o com {@code PessoaEntity} Ã©
 * N:N gerenciada pela tabela de junÃ§Ã£o {@code pessoa_roles}.
 *
 * <p>
 * Camada: infrastructure/persistence/jpa.
 *
 * <p>
 * Fonte: Spring Security Reference â€” Granting Authority, Baeldung "Spring
 * Security â€” Roles and Privileges".
 */
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class RoleEntity extends BaseEntity<Long> {

    /**
     * Nome da role armazenado como String (ex.: {@code "ROLE_ADMIN"}). Ãšnico para
     * evitar duplicatas na tabela de referÃªncia.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "nome", length = 30, nullable = false, unique = true)
    private RoleEnum nome;
}
