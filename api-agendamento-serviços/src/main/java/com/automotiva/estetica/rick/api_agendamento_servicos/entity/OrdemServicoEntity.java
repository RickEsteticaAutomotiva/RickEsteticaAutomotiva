package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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

    @Column(name = "dtConclusao", updatable = true)
    private Date dtConclusao;

    @Column(name = "observacoes", length = 255)
    private String observacoes;

    @Column(name = "status", length = 40, nullable = false)
    private String status;

    @Column(name = "fkAgendamento",nullable = false)
    private Long idAgendamento;
}