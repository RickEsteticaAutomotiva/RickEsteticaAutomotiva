package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.NotNull;
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
public class FavoritoRequest {

    @NotNull(message = "O ID da pessoa é obrigatório")
    private Long idPessoa;

    @NotNull(message = "O ID do serviço é obrigatório")
    private Long idServico;
}
