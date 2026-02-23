package com.automotiva.estetica.rick.adapter.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "motivo")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MotivoCancelamentoJpaEntity extends BaseJpaEntity<Long> {

    @Column(nullable = false, unique = true, length = 50)
    private String descricao;
}
