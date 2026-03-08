package com.automotiva.estetica.rick.domain.entity;

import java.math.BigDecimal;
import java.time.LocalTime;
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
public class Servico {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagem;
    private LocalTime duracaoHoras;
    private Categoria categoria;

    public void atualizar(
            String nome, String descricao, BigDecimal preco, String imagem, Long categoriaId,
            LocalTime duracaoHoras) {
        if (nome != null) this.nome = nome;
        if (descricao != null) this.descricao = descricao;
        if (preco != null) this.preco = preco;
        if (imagem != null) this.imagem = imagem;
        if (categoriaId != null) this.categoria = Categoria.builder().id(categoriaId).build();
        if (duracaoHoras != null) this.duracaoHoras = duracaoHoras;
    }
}
