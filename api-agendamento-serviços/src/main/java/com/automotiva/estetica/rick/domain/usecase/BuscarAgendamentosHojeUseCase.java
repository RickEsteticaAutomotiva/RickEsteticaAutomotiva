package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarAgendamentosHojeUseCase {

    private final OrdemServicoGateway ordemServicoGateway;

    public List<OrdemServico> execute(LocalDate data) {
        return ordemServicoGateway.buscarAgendamentosDodia(data);
    }
}
