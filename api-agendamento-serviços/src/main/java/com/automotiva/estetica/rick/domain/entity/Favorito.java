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
public class Favorito {

    private Long id;
    private Pessoa pessoa;
    private Servico servico;

    /**
     * Regra de domínio: um favorito sempre exige pessoa e serviço.
     */
    public static Favorito criar(Pessoa pessoa, Servico servico) {
        if (pessoa == null) throw new IllegalArgumentException("Pessoa não pode ser nula no favorito");
        if (servico == null) throw new IllegalArgumentException("Serviço não pode ser nulo no favorito");
        return Favorito.builder().pessoa(pessoa).servico(servico).build();
    }
}
