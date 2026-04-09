package com.automotiva.estetica.rick.infrastructure.entity;

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
public class FavoritoEntity extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_pessoa", nullable = false)
    private PessoaEntity pessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_servico", nullable = false)
    private ServicoEntity servico;
}
