package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "agendamento")
public class AgendamentoEntity extends BaseEntity<Long> {

    private LocalDateTime dataHora;

    private String status;

    @ManyToOne
    @JoinColumn(name = "fkVeiculo")
    private VeiculoEntity veiculo;

    private String googleEventId;
}
