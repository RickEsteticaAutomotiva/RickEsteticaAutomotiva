package com.automotiva.estetica.rick.application.assembler;

import com.automotiva.estetica.rick.application.dto.response.CampoExtraidoResponse;
import com.automotiva.estetica.rick.application.dto.response.ImportarAgendamentoResponse;
import com.automotiva.estetica.rick.application.mapper.PessoaDTOMapper;
import com.automotiva.estetica.rick.application.mapper.ServicoDTOMapper;
import com.automotiva.estetica.rick.application.mapper.VeiculoDTOMapper;
import com.automotiva.estetica.rick.domain.entity.CampoExtraido;
import com.automotiva.estetica.rick.domain.entity.CandidatosAgendamento;
import com.automotiva.estetica.rick.domain.entity.DadosAgendamentoExtraido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportacaoAgendamentoResponseAssembler {

    private final PessoaDTOMapper pessoaDTOMapper;
    private final VeiculoDTOMapper veiculoDTOMapper;
    private final ServicoDTOMapper servicoDTOMapper;

    public ImportarAgendamentoResponse toResponse(DadosAgendamentoExtraido dados, CandidatosAgendamento candidatos) {
        return ImportarAgendamentoResponse.builder().nomeCliente(toCampoResponse(dados.getNomeCliente()))
                .telefoneCliente(toCampoResponse(dados.getTelefoneCliente()))
                .placaVeiculo(toCampoResponse(dados.getPlacaVeiculo()))
                .modeloVeiculo(toCampoResponse(dados.getModeloVeiculo()))
                .descricaoServico(toCampoResponse(dados.getDescricaoServico())).data(toCampoResponse(dados.getData()))
                .horario(toCampoResponse(dados.getHorario())).valor(toCampoResponse(dados.getValor()))
                .observacoesLivres(dados.getObservacoesLivres())
                .candidatosPessoa(candidatos.getPessoas().stream().map(pessoaDTOMapper::toResponse).toList())
                .candidatosVeiculo(candidatos.getVeiculos().stream().map(veiculoDTOMapper::toResponse).toList())
                .candidatosServico(candidatos.getServicos().stream().map(servicoDTOMapper::toResponse).toList())
                .build();
    }

    private <T> CampoExtraidoResponse<T> toCampoResponse(CampoExtraido<T> campo) {
        if (campo == null) {
            return CampoExtraidoResponse.<T>builder().valor(null).confianca(null).build();
        }
        return CampoExtraidoResponse.<T>builder().valor(campo.getValor()).confianca(campo.getConfianca()).build();
    }
}
