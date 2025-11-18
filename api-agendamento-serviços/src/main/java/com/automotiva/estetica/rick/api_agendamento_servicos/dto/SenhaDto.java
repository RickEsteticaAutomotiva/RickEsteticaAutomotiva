package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SenhaDto {
    private String senhaAtual;
    private String novaSenha;
}