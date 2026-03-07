package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;

public interface OrdemServicoEventPublisherPort {
    void publicarOrdemServicoCriada(
            OrdemServico ordemServico, OrdemServicoRequest ordemServicoRequest);
}
