package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "motivo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MotivoCancelamentoEntity extends BaseEntity{
    @Column(nullable = false, unique = true, length = 50)
    private String descricao;
}
