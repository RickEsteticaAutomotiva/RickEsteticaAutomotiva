package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    private String email;
    private String senha;
}
