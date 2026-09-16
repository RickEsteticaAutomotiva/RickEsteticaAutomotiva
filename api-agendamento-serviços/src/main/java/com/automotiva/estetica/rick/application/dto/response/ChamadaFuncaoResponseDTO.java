package com.automotiva.estetica.rick.application.dto.response;

import java.util.Map;
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
public class ChamadaFuncaoResponseDTO {

    private String nome;
    private Map<String, Object> argumentos;
}
