package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_servico")
public class ItemServicoEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServicoEntity ordemServico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id", nullable = false)
    private ServicoEntity servico;

    @Column(name = "preco", nullable = false)
    private BigDecimal preco;
}