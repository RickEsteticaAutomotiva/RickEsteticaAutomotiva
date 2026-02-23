package com.automotiva.estetica.rick.domain.enums;

/**
 * Roles disponíveis no sistema.
 *
 * <p>O prefixo {@code ROLE_} é exigido pelo Spring Security para que
 * {@code hasRole('ADMIN')} funcione corretamente (ele concatena o prefixo
 * internamente). Fonte: Spring Security Reference — 5.4 Authorization.
 *
 * <p>Camada: domain/enums (sem dependência de framework).
 */
public enum RoleEnum {
    ROLE_ADMIN,
    ROLE_GERENTE,
    ROLE_CLIENTE;

    /** Retorna o nome da role como String, ex.: {@code "ROLE_ADMIN"}. */
    public String authority() {
        return this.name();
    }
}

