package com.automotiva.estetica.rick.application.dto.request;

import java.time.LocalDate;
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
public class PessoaAtualizacaoRequest {

    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
}
