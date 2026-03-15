package com.automotiva.estetica.rick.application.dto.response;

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
public class FaturamentoServicoResponse {
    private String categoria;
    private List<FaturamentoServicoItemResponse> servicos;
}
