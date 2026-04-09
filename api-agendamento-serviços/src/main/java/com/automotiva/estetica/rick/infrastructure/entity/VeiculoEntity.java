package com.automotiva.estetica.rick.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "veiculo")
@SQLRestriction("deletado_em IS NULL")
public class VeiculoEntity extends BaseEntity<Long> {

    @Column(name = "placa")
    private String placa;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "marca")
    private String marca;

    @Column(name = "porte")
    private String porte;

    @Column(name = "cor")
    private String cor;

    @Size(min = 4, max = 4)
    @Pattern(regexp = "\\d{4}")
    @Column(name = "ano", length = 4)
    private String ano;

    /**
     * Data e hora em que o veÃ­culo foi inativado (soft delete). {@code null}
     * indica que o registro estÃ¡ ativo.
     */
    @Column(name = "deletado_em")
    private LocalDateTime deletadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usuario")
    private PessoaEntity pessoa;
}
