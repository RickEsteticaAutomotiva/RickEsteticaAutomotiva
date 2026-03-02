package com.automotiva.estetica.rick.domain.entity;

import com.automotiva.estetica.rick.domain.enums.StatusEmailEnum;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    private Long id;
    private Pessoa pessoa;
    private String remetente;
    private String destinatario;
    private String comCopia;
    private String comCopiaOculta;
    private String assunto;
    private String corpo;
    private LocalDateTime dataEnvioEmail;
    private StatusEmailEnum statusEmail;
    private List<byte[]> anexos;
    private List<String> nomesAnexos;
}
