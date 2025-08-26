package com.automotiva.estetica.rick.api_agendamento_servicos.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaDto {
    private Long id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
}
