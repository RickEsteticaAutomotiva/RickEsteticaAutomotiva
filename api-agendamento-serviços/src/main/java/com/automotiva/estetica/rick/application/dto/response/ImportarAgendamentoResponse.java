package com.automotiva.estetica.rick.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resultado da extração + matching de uma imagem de agendamento. Os
 * candidatos reaproveitam os DTOs de resposta já usados pelos endpoints de
 * Pessoa/Veículo/Serviço — nenhum dado aqui é inventado ou persistido; é
 * apenas uma sugestão para revisão do gerente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportarAgendamentoResponse {

    private CampoExtraidoResponse<String> nomeCliente;
    private CampoExtraidoResponse<String> telefoneCliente;
    private CampoExtraidoResponse<String> placaVeiculo;
    private CampoExtraidoResponse<String> modeloVeiculo;
    private CampoExtraidoResponse<String> descricaoServico;
    private CampoExtraidoResponse<LocalDate> data;
    private CampoExtraidoResponse<LocalTime> horario;
    private CampoExtraidoResponse<BigDecimal> valor;
    private String observacoesLivres;

    private List<PessoaResponse> candidatosPessoa;
    private List<VeiculoResponse> candidatosVeiculo;
    private List<ServicoResponse> candidatosServico;
}
