package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "veiculo")
//public class VeiculoEntity extends BaseEntity {
public class VeiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private String placa;

    private String modelo;

    private String marca;

    private String porte;

    private String cor;

    @Size(min = 4, max = 4)
    @Pattern(regexp = "\\d{4}")
    private String ano;

    @ManyToOne
    @JoinColumn(name = "fkUsuario")
    private PessoaEntity pessoa; // TODO TROCAR DEPOIS PARA USUARIO

}
