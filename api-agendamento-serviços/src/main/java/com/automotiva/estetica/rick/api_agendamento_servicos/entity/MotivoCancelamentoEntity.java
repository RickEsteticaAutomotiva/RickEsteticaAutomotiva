package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "motivo")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MotivoCancelamentoEntity extends BaseEntity<Long> {
    @Column(nullable = false, unique = true, length = 50)
    private String descricao;
}
