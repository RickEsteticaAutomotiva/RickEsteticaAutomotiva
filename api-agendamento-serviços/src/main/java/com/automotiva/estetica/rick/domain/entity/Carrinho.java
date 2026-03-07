package com.automotiva.estetica.rick.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carrinho {

    private Long id;
    private Pessoa pessoa;
    private Servico servico;

    /** Regra de domínio: um item de carrinho sempre exige pessoa e serviço. */
    public static Carrinho criar(Pessoa pessoa, Servico servico) {
        if (pessoa == null)
            throw new IllegalArgumentException("Pessoa não pode ser nula no carrinho");
        if (servico == null)
            throw new IllegalArgumentException("Serviço não pode ser nulo no carrinho");
        return Carrinho.builder().pessoa(pessoa).servico(servico).build();
    }
}
