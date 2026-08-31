package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class MensagemAssistenteDTO {

    @NotBlank(message = "O papel da mensagem é obrigatório")
    private String papel;

    @Valid
    @NotEmpty(message = "A mensagem precisa ter ao menos uma parte")
    private List<ParteMensagemDTO> partes;
}
