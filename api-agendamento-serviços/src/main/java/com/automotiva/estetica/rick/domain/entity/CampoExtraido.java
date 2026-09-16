package com.automotiva.estetica.rick.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Um campo extraído de uma imagem por IA, junto do nível de confiança da
 * extração.
 *
 * <p>
 * {@code valor == null} é sempre um resultado válido — significa que a IA não
 * conseguiu identificar o dado com segurança. Nunca é preenchido com um valor
 * inventado; nesse caso {@code confianca} também é {@code null}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampoExtraido<T> {

    private T valor;
    private Double confianca;
}
