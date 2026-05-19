package com.automotiva.estetica.rick.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria implements Serializable {

    private Long id;
    private String nome;

    /**
     * Atualiza os dados da categoria. Regra de domínio: o nome não pode ser nulo
     * nem em branco.
     */
    public void atualizar(String nome) {
        if (nome != null && !nome.isBlank())
            this.nome = nome;
    }
}
