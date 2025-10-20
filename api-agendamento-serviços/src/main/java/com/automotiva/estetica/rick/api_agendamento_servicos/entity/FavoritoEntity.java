package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favorito")
public class FavoritoEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usuario", nullable = false)
    private PessoaEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_servico", nullable = false)
    private ServicoEntity servico;
}
