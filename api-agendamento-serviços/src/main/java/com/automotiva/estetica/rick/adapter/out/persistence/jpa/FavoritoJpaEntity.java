package com.automotiva.estetica.rick.adapter.out.persistence.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favorito")
public class FavoritoJpaEntity extends BaseJpaEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_pessoa", nullable = false)
    private PessoaJpaEntity pessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_servico", nullable = false)
    private ServicoJpaEntity servico;
}
