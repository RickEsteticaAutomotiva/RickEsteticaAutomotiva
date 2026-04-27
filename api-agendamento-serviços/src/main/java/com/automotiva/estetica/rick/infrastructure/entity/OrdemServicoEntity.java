package com.automotiva.estetica.rick.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @Column(name = "preco_minimo", nullable = false)
    private BigDecimal precoMinimo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_veiculo", nullable = false)
    private VeiculoEntity veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_status", nullable = false)
    private StatusEntity status;

    @Column(name = "observacoes", length = 255)
    private String observacoes;

    @Column(name = "dt_conclusao")
    private LocalDateTime dtConclusao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_motivo")
    private MotivoCancelamentoEntity motivoCancelamento;
}

