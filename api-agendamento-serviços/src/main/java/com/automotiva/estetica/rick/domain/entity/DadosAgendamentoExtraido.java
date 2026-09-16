package com.automotiva.estetica.rick.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dados de um agendamento extraídos de uma imagem (anotação, print de
 * WhatsApp, etc.) por IA multimodal. Cada campo é um {@link CampoExtraido},
 * podendo vir {@code null} quando a IA não identificou o dado com segurança —
 * nunca é inventado.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosAgendamentoExtraido {

    private CampoExtraido<String> nomeCliente;
    private CampoExtraido<String> telefoneCliente;
    private CampoExtraido<String> placaVeiculo;
    private CampoExtraido<String> modeloVeiculo;
    private CampoExtraido<String> descricaoServico;
    private CampoExtraido<LocalDate> data;
    private CampoExtraido<LocalTime> horario;
    private CampoExtraido<BigDecimal> valor;
    private String observacoesLivres;
}
