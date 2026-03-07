package com.automotiva.estetica.rick.adapter.out.persistence.jpaentity;

import com.automotiva.estetica.rick.domain.enums.StatusEmailEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
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
@Table(name = "email")
public class EmailJpaEntity extends BaseJpaEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id")
    private PessoaJpaEntity pessoa;

    private String remetente;

    private String destinatario;

    private String comCopia;

    private String comCopiaOculta;

    private String assunto;

    @Column(columnDefinition = "TEXT")
    private String corpo;

    private LocalDateTime dataEnvioEmail;

    @Enumerated(EnumType.STRING)
    private StatusEmailEnum statusEmail;

    @Transient private List<byte[]> anexos;

    @Transient private List<String> nomesAnexos;
}
