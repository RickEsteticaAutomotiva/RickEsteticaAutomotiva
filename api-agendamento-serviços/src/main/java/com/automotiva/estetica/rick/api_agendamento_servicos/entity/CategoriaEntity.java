package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categoria")
public class CategoriaEntity extends BaseEntity{
    private String nome;
}
