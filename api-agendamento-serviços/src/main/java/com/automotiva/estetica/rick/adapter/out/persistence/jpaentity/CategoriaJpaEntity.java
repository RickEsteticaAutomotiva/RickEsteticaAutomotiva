package com.automotiva.estetica.rick.adapter.out.persistence.jpaentity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaJpaEntity extends BaseJpaEntity<Long> {

    @Column(name = "nome", length = 100)
    private String nome;
}
