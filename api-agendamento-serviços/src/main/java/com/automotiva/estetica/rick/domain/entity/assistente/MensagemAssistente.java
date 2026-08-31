package com.automotiva.estetica.rick.domain.entity.assistente;

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
public class MensagemAssistente {

    private PapelMensagem papel;
    private List<ParteMensagem> partes;
}
