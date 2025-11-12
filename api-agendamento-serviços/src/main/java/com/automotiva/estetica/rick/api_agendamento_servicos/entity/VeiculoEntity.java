package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "veiculo")
public class VeiculoEntity extends BaseEntity<Long> {

    private String placa;

    private String modelo;

    private String marca;

    private String porte;

    private String cor;

    @Size(min = 4, max = 4)
    @Pattern(regexp = "\\d{4}")
    private String ano;

    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    private PessoaEntity pessoa; // TODO TROCAR DEPOIS PARA USUARIO
}
