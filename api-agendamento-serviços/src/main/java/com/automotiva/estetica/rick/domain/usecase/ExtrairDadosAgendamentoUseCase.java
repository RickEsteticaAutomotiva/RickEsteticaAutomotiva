package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.DadosAgendamentoExtraido;
import com.automotiva.estetica.rick.domain.gateway.ExtratorAgendamentoGateway;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExtrairDadosAgendamentoUseCase {

    private static final ZoneId ZONE_ID_SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private final ExtratorAgendamentoGateway extratorAgendamentoGateway;

    public DadosAgendamentoExtraido execute(String imagemBase64, String mimeType) {
        LocalDate dataReferencia = LocalDate.now(ZONE_ID_SAO_PAULO);
        return extratorAgendamentoGateway.extrair(imagemBase64, mimeType, dataReferencia);
    }
}
