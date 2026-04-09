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
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "servico")
@SQLRestriction("deletado_em IS NULL")
public class ServicoEntity extends BaseEntity<Long> {

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "preco", nullable = false)
    private BigDecimal preco;

    @Column(name = "imagem")
    private String imagem;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    /**
     * Data e hora em que o serviÃ§o foi inativado (soft delete). {@code null}
     * indica que o registro estÃ¡ ativo.
     */
    @Column(name = "deletado_em")
    private LocalDateTime deletadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_categoria", nullable = false)
    private CategoriaEntity categoria;
}
