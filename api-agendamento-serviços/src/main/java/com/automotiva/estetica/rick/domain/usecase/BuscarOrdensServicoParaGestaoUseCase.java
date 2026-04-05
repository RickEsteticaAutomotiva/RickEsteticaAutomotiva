package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarOrdensServicoParaGestaoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;

    public Page<OrdemServico> execute(Long status, LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw CampoInvalidoException.builder().mensagem("dataInicio não pode ser maior que dataFim").detalhes("")
                    .build();
        }

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : null;

        return ordemServicoGateway.buscarTodosParaGestao(status, inicio, fim, pageable);
    }
}
