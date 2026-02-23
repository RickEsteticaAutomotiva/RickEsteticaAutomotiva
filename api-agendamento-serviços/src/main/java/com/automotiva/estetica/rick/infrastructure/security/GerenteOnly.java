package com.automotiva.estetica.rick.infrastructure.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Meta-anotação de segurança que restringe o acesso aos roles GERENTE e ADMIN.
 *
 * <p>ADMIN é incluído pois é hierarquicamente superior e deve ter acesso
 * a todo recurso de gerência. Para mais detalhes sobre multi-role no Spring
 * Security, consulte: https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
 *
 * <p>Pode ser aplicada na classe (protege todos os métodos) ou em métodos individuais.
 *
 * <p>Camada: infrastructure/security.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("hasRole('GERENTE')")
public @interface GerenteOnly {}

