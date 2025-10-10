package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaCadastroDto {
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String senha;
}
