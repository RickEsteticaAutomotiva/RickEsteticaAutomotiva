package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.CancelamentoMotivoResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoDiarioResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoResumo;
import com.automotiva.estetica.rick.domain.entity.ProximoAgendamentoResumo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DashboardGateway {

    BigDecimal somarFaturamentoDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    Integer buscarQtdOrdensDoMes(LocalDateTime inicio, LocalDateTime fim);

    Integer buscarQtdOrdensConcluidasNoMes(LocalDateTime inicio, LocalDateTime fim);

    BigDecimal calcularTicketMedioDoMes(LocalDateTime inicio, LocalDateTime fim);

    BigDecimal somarReceitaRecebidaDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    BigDecimal somarCustoRealizadoDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    List<CancelamentoMotivoResumo> buscarCancelamentosPorMotivoDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    List<FaturamentoDiarioResumo> buscarFaturamentoPorDia(LocalDateTime dataInicial);

    List<FaturamentoServicoResumo> buscarFaturamentoServicos(LocalDateTime inicio, LocalDateTime fim);

    long contarAgendamentosNoPeriodoExcetoStatus(LocalDateTime inicio, LocalDateTime fim, Long statusIdIgnorado);

    BigDecimal somarFaturamentoEstimadoNoPeriodoExcetoStatus(LocalDateTime inicio, LocalDateTime fim,
            Long statusIdIgnorado);

    Optional<ProximoAgendamentoResumo> buscarProximoAgendamentoNoPeriodoExcetoStatus(LocalDateTime inicio,
            LocalDateTime fim, Long statusIdIgnorado);
}
