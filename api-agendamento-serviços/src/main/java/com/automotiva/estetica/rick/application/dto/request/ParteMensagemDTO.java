package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.Size;
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
public class ParteMensagemDTO {

    @Size(max = 4000, message = "O texto da mensagem é muito longo")
    private String texto;

    private ChamadaFuncaoRequestDTO chamadaFuncao;

    private RespostaFuncaoDTO respostaFuncao;
}
