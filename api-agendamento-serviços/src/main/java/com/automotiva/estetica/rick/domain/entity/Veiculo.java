package com.automotiva.estetica.rick.domain.entity;

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
public class Veiculo {

    private Long id;
    private String placa;
    private String modelo;
    private String marca;
    private String porte;
    private String cor;
    private String ano;
    private Pessoa pessoa;

    public void atualizar(String placa, String modelo, String marca, String porte, String cor, String ano) {
        if (placa != null)
            this.placa = placa;
        if (modelo != null)
            this.modelo = modelo;
        if (marca != null)
            this.marca = marca;
        if (porte != null)
            this.porte = porte;
        if (cor != null)
            this.cor = cor;
        if (ano != null)
            this.ano = ano;
    }
}
