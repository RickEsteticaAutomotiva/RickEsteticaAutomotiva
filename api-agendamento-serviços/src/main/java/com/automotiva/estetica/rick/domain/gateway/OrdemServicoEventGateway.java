package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import java.util.List;

public interface OrdemServicoEventGateway {

    void publicarOrdemServicoCriada(OrdemServico ordemServico, List<String> nomesServicos);

    void publicarOrdemServicoAtualizada(OrdemServico ordemServico);
}
