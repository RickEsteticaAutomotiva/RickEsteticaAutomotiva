package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.assembler.ImportacaoAgendamentoResponseAssembler;
import com.automotiva.estetica.rick.application.dto.request.ExtrairAgendamentoRequest;
import com.automotiva.estetica.rick.application.dto.response.ImportarAgendamentoResponse;
import com.automotiva.estetica.rick.domain.entity.CandidatosAgendamento;
import com.automotiva.estetica.rick.domain.entity.DadosAgendamentoExtraido;
import com.automotiva.estetica.rick.domain.usecase.BuscarCandidatosAgendamentoUseCase;
import com.automotiva.estetica.rick.domain.usecase.ExtrairDadosAgendamentoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImportacaoAgendamentoApplicationService {

    private final ExtrairDadosAgendamentoUseCase extrairDadosAgendamentoUseCase;
    private final BuscarCandidatosAgendamentoUseCase buscarCandidatosAgendamentoUseCase;
    private final ImportacaoAgendamentoResponseAssembler assembler;

    public ImportarAgendamentoResponse importar(ExtrairAgendamentoRequest request) {
        DadosAgendamentoExtraido dados = extrairDadosAgendamentoUseCase.execute(request.getImagemBase64(),
                request.getMimeType());
        CandidatosAgendamento candidatos = buscarCandidatosAgendamentoUseCase.execute(dados);
        return assembler.toResponse(dados, candidatos);
    }
}
