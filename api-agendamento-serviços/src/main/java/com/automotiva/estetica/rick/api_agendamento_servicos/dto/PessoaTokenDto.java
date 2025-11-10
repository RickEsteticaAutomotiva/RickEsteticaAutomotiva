package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaTokenDto {
    private Long id;
    private String email;
    private String nome;
    private String token;
}
