package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categoria")
public class CategoriaEntity extends BaseEntity<Long> {
    private String nome;
}
