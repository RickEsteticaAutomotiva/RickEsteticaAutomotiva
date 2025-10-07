package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "servico")
public class ServicoEntity extends BaseEntity {

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "preco", nullable = false)
    private BigDecimal preco;
}