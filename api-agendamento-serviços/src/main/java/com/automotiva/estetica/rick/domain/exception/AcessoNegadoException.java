package com.automotiva.estetica.rick.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando usuário tenta acessar recurso sem permissão.
 *
 * Corresponde a HTTP 403 (Forbidden) — OWASP A01 (Broken Access Control).
 *
 * @see <a href="https://owasp.org/Top10/A01_2021-Broken_Access_Control/">OWASP
 *      A01</a>
 */
public class AcessoNegadoException extends DomainException {

    public AcessoNegadoException(String tipo, String mensagem, String detalhes, HttpStatus status) {
        super(tipo, mensagem, detalhes, status);
    }

    public static class Builder extends DomainException.Builder<AcessoNegadoException, Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public AcessoNegadoException build() {
            this.tipo = "ACESSO_NEGADO";
            this.status = HttpStatus.FORBIDDEN;
            return new AcessoNegadoException(tipo, mensagem, detalhes, status);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
