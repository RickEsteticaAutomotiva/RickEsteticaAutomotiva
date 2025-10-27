package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ordem_servico")
//public class OrdemServicoEntity extends BaseEntity {
public class OrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

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