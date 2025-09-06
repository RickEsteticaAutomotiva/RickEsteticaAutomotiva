package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ordem_servico")
public class OrdemServicoEntity extends BaseEntity {

    @Column(name = "dtConclusao", updatable = true)
    private Date dtConclusao;

    @Column(name = "observacoes", length = 255)
    private String observacoes;

    @Column(name = "status", length = 40, nullable = false)
    private String status;
}