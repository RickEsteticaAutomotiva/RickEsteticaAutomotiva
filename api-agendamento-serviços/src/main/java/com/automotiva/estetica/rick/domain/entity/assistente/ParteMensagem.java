package com.automotiva.estetica.rick.domain.entity.assistente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Uma parte de mensagem tem exatamente um dos três campos preenchido: texto
 * livre, uma chamada de função proposta pelo modelo, ou o resultado de uma
 * função já executada pelo cliente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParteMensagem {

    private String texto;
    private ChamadaFuncao chamadaFuncao;
    private RespostaFuncao respostaFuncao;
}
