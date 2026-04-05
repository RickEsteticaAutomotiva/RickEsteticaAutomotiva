package com.automotiva.estetica.rick.infrastructure.security;

import com.automotiva.estetica.rick.domain.exception.AcessoNegadoException;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utilitário para obter informações do usuário autenticado via SecurityContext.
 *
 * <p>
 * Extrai o username (email) do JWT e mapeia para o ID da pessoa no banco de
 * dados.
 *
 * <p>
 * Camada: infrastructure/security.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    @Lazy
    private final PessoaGateway pessoaGateway;

    /**
     * Obtém o ID da pessoa autenticada atual.
     *
     * @return ID da pessoa autenticada
     * @throws AcessoNegadoException
     *             se nenhum usuário estiver autenticado
     */
    public Long obterIdUsuarioAutenticado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw AcessoNegadoException.builder().mensagem("usuário não autenticado").detalhes("").build();
        }

        String email = authentication.getName();

        return pessoaGateway.buscarPorEmail(email).map(pessoa -> pessoa.getId()).orElseThrow(() -> AcessoNegadoException
                .builder().mensagem("usuário autenticado não encontrado no sistema").detalhes("").build());
    }

    /**
     * Obtém o username (email) da pessoa autenticada.
     *
     * @return email da pessoa autenticada
     * @throws AcessoNegadoException
     *             se nenhum usuário estiver autenticado
     */
    public String obterUsernameAutenticado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw AcessoNegadoException.builder().mensagem("usuário não autenticado").detalhes("").build();
        }

        return authentication.getName();
    }
}
