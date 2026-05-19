package com.automotiva.estetica.rick.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class Servico implements Serializable {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagem;
    private Integer duracaoMinutos;
    private Categoria categoria;

    public void atualizar(String nome, String descricao, BigDecimal preco, String imagem, Long categoriaId,
            Integer duracaoMinutos) {
        if (nome != null)
            this.nome = nome;
        if (descricao != null)
            this.descricao = descricao;
        if (preco != null)
            this.preco = preco;
        if (imagem != null)
            this.imagem = imagem;
        if (categoriaId != null)
            this.categoria = Categoria.builder().id(categoriaId).build();
        if (duracaoMinutos != null)
            this.duracaoMinutos = duracaoMinutos;
    }

}
