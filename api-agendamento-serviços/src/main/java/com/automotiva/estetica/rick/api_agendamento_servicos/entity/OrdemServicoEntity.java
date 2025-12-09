package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ordem_servico")
public class OrdemServicoEntity extends BaseEntity<Long> {

    @Column(name = "data_agendamento", nullable = false)
    private LocalDateTime dataAgendamento;

    @Column(name = "precoMinimo", nullable = false)
    private BigDecimal precoMinimo;

    @ManyToOne
    @JoinColumn(name = "fk_veiculo", nullable = false)
    private VeiculoEntity veiculo;

    @ManyToOne
    @JoinColumn(name = "fk_status", nullable = false)
    private StatusEntity status;

    @Column(name = "observacoes", length = 255)
    private String observacoes;

    @Column(name = "dt_conclusao", updatable = true)
    private LocalDateTime dtConclusao;

    @ManyToOne
    @JoinColumn(name = "fk_motivo", nullable = true)
    private MotivoCancelamentoEntity motivoCancelamento;
}
