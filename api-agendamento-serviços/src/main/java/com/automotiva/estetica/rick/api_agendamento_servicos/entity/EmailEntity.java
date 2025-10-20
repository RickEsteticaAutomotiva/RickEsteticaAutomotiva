package com.automotiva.estetica.rick.api_agendamento_servicos.entity;

import com.automotiva.estetica.rick.api_agendamento_servicos.enums.StatusEmailEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email")
public class EmailEntity extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private PessoaEntity pessoa;
    private String remetente;
    @NotBlank(message = "O email de destino é obrigatório")
    @Email(message = "Email de destino inválido")
    private String destinatario;
    private String comCopia;
    private String comCopiaOculta;
    @NotBlank(message = "O assunto é obrigatório")
    private String assunto;
    @Column(columnDefinition = "TEXT")
    private String corpo;
    private LocalDateTime dataEnvioEmail;
    @Enumerated(EnumType.STRING)
    private StatusEmailEnum statusEmail;
    @Transient
    private List<byte[]> anexos;
    @Transient
    private List<String> nomesAnexos;
}
