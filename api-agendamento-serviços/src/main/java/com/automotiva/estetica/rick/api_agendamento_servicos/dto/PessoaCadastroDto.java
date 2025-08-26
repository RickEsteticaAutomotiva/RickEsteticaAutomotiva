package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PessoaCadastroDto {
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String senha;
}
