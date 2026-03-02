package com.automotiva.estetica.rick.application.service;

import org.springframework.security.core.Authentication;

/**
 * Porta interna para geração e validação de tokens JWT.
 * A implementação vive em infrastructure/security/.
 */
public interface JwtService {

    String gerarToken(Authentication authentication);

    String obterUsernameDoToken(String token);

    boolean tokenValido(String token, org.springframework.security.core.userdetails.UserDetails userDetails);
}
