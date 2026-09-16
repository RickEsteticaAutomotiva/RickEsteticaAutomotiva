package com.automotiva.estetica.rick.domain.entity.assistente;

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
public class ChamadaFuncao {

    private String nome;
    private Map<String, Object> argumentos;
}
