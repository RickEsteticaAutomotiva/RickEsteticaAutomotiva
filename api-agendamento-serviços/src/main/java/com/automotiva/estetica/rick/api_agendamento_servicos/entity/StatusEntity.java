package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "status")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StatusEntity extends BaseEntity<Long> {
    @Column(nullable = false, unique = true, length = 50)
    private String descricao;
}
