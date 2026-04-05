package com.automotiva.estetica.rick.application.security;

import com.automotiva.estetica.rick.domain.gateway.UsuarioAutenticadoGateway;
import com.automotiva.estetica.rick.domain.exception.AcessoNegadoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validador de propriedade de recursos (ownership) — OWASP A01 (Broken Access
 * Control).
 *
 * <p>
 * Garante que um usuário só consiga acessar/modificar recursos que lhe
 * pertencem.
 *
 * <p>
 * Camada: application/security.
 *
 * @see <a href="https://owasp.org/Top10/A01_2021-Broken_Access_Control/">OWASP
 *      A01</a>
 */
@Component
@RequiredArgsConstructor
public class OwnershipValidator {

    private final UsuarioAutenticadoGateway usuarioAutenticadoGateway;

    /**
     * Valida se o usuário autenticado é o proprietário do recurso.
     *
     * <p>
     * Compara o ID do recurso com o ID da pessoa autenticada. Se forem diferentes,
     * lança {@link AcessoNegadoException} com HTTP 403 (Forbidden).
     *
     * @param recursoId
     *            ID do recurso a ser acessado/modificado
     * @throws AcessoNegadoException
     *             se o usuário não é o proprietário
     *
     *             Exemplo de uso:
     *
     *             <pre>
     *             // No controller
     *             ownershipValidator.validarPropriedade(pessoaId);
     *             pessoaUseCase.atualizar(pessoaId, request);
     *             </pre>
     */
    public void validarPropriedade(Long recursoId) {
        Long usuarioAutenticadoId = usuarioAutenticadoGateway.obterIdUsuarioAutenticado();

        if (!usuarioAutenticadoId.equals(recursoId)) {
            throw AcessoNegadoException.builder().mensagem("você não tem permissão para acessar este recurso")
                    .detalhes("tentativa de acesso a recurso de outro usuário").build();
        }
    }

    /**
     * Valida se o recurso pertence a uma pessoa específica.
     *
     * <p>
     * Útil para validar recursos aninhados (ex: veículo que pertence a uma pessoa).
     *
     * @param recursoId
     *            ID do recurso
     * @param pessoaDonoId
     *            ID da pessoa dona do recurso
     * @throws AcessoNegadoException
     *             se o usuário não é o dono
     */
    public void validarPropriedadePessoa(Long recursoId, Long pessoaDonoId) {
        Long usuarioAutenticadoId = usuarioAutenticadoGateway.obterIdUsuarioAutenticado();

        if (!usuarioAutenticadoId.equals(pessoaDonoId)) {
            throw AcessoNegadoException.builder().mensagem("você não tem permissão para acessar este recurso")
                    .detalhes("tentativa de acesso a recurso que não lhe pertence").build();
        }
    }
}
