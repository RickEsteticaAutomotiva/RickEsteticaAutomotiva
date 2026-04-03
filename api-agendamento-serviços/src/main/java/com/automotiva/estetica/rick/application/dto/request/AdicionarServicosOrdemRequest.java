package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.Valid;
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
public class AdicionarServicosOrdemRequest {

    @NotEmpty(message = "servicos e obrigatorio")
    @Valid
    private List<ServicoAplicadoRequest> servicos;
}
