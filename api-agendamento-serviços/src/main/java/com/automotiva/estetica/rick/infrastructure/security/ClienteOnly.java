package com.automotiva.estetica.rick.infrastructure.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Meta-anotação de segurança que restringe o acesso exclusivamente ao role USER (cliente).
 *
 * <p>Pode ser aplicada na classe (protege todos os métodos) ou em métodos individuais.
 *
 * <p>Para adicionar ou remover roles, edite apenas o {@code @PreAuthorize} aqui. Nenhum controller
 * precisa ser modificado.
 *
 * <p>Camada: infrastructure/security.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("hasRole('CLIENTE')")
public @interface ClienteOnly {}
