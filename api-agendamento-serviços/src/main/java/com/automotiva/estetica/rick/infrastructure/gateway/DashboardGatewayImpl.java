package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.infrastructure.repository.ordemservico.OrdemServicoRepository;
import com.automotiva.estetica.rick.domain.entity.CancelamentoMotivoResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoDiarioResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoResumo;
import com.automotiva.estetica.rick.domain.entity.ProximoAgendamentoResumo;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardGatewayImpl implements DashboardGateway {

    private final OrdemServicoRepository ordemServicoRepository;

    @Override
    public BigDecimal somarFaturamentoDoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return ordemServicoRepository.somarFaturamentoDoPeriodo(inicio, fim);
    }

    @Override
    public Integer buscarQtdOrdensDoMes(LocalDateTime inicio, LocalDateTime fim) {
        return ordemServicoRepository.buscarQtdOrdensDoMes(inicio, fim);
    }

    @Override
    public Integer buscarQtdOrdensConcluidasNoMes(LocalDateTime inicio, LocalDateTime fim) {
        return ordemServicoRepository.buscarQtdOrdensConcluidasNoMes(inicio, fim);
    }

    @Override
    public BigDecimal calcularTicketMedioDoMes(LocalDateTime inicio, LocalDateTime fim) {
        return ordemServicoRepository.calcularTicketMedioDoMes(inicio, fim);
    }

    @Override
    public BigDecimal somarReceitaRecebidaDoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return ordemServicoRepository.somarReceitaRecebidaDoPeriodo(inicio, fim);
    }

    @Override
    public BigDecimal somarCustoRealizadoDoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return ordemServicoRepository.somarCustoRealizadoDoPeriodo(inicio, fim);
    }

    @Override
    public List<CancelamentoMotivoResumo> buscarCancelamentosPorMotivoDoPeriodo(LocalDateTime inicio,
            LocalDateTime fim) {
        return ordemServicoRepository.buscarCancelamentosPorMotivoDoPeriodo(inicio, fim).stream()
                .map(row -> new CancelamentoMotivoResumo((String) row[0],
                        row[1] == null ? 0L : ((Number) row[1]).longValue()))
                .toList();
    }

    @Override
    public List<FaturamentoDiarioResumo> buscarFaturamentoPorDia(LocalDateTime dataInicial) {
        return ordemServicoRepository.buscarFaturamentoPorDia(dataInicial).stream()
                .map(row -> new FaturamentoDiarioResumo(((java.sql.Date) row[0]).toLocalDate(), (BigDecimal) row[1]))
                .toList();
    }

    @Override
    public List<FaturamentoServicoResumo> buscarFaturamentoServicos(LocalDateTime inicio, LocalDateTime fim) {
        return ordemServicoRepository.buscarFaturamentoServicos(inicio, fim).stream()
                .map(row -> new FaturamentoServicoResumo(row[0] == null ? null : ((Number) row[0]).longValue(),
                        (String) row[1], row[2] == null ? null : ((Number) row[2]).longValue(), (String) row[3],
                        row[4] == null ? 0L : ((Number) row[4]).longValue(), (BigDecimal) row[5]))
                .toList();
    }

    @Override
    public long contarAgendamentosNoPeriodoExcetoStatus(LocalDateTime inicio, LocalDateTime fim,
            Long statusIdIgnorado) {
        return ordemServicoRepository.contarAgendamentosNoPeriodoExcetoStatus(inicio, fim, statusIdIgnorado);
    }

    @Override
    public BigDecimal somarFaturamentoEstimadoNoPeriodoExcetoStatus(LocalDateTime inicio, LocalDateTime fim,
            Long statusIdIgnorado) {
        return ordemServicoRepository.somarFaturamentoEstimadoNoPeriodoExcetoStatus(inicio, fim, statusIdIgnorado);
    }

    @Override
    public Optional<ProximoAgendamentoResumo> buscarProximoAgendamentoNoPeriodoExcetoStatus(LocalDateTime inicio,
            LocalDateTime fim, Long statusIdIgnorado) {
        return ordemServicoRepository.findFirstByDataAgendamentoBetweenAndStatus_IdNotOrderByDataAgendamentoAscIdAsc(
                inicio, fim, statusIdIgnorado).map(ordem -> {
                    String servicoPrincipal = ordemServicoRepository.buscarNomesServicosDaOrdem(ordem.getId()).stream()
                            .findFirst().orElse("");

                    return new ProximoAgendamentoResumo(ordem.getId(), servicoPrincipal, ordem.getDataAgendamento(),
                            ordem.getVeiculo() != null && ordem.getVeiculo().getPessoa() != null
                                    ? ordem.getVeiculo().getPessoa().getNome()
                                    : "",
                            ordem.getVeiculo() != null ? ordem.getVeiculo().getMarca() : null,
                            ordem.getVeiculo() != null ? ordem.getVeiculo().getModelo() : null,
                            ordem.getVeiculo() != null ? ordem.getVeiculo().getPlaca() : null,
                            ordem.getStatus() != null ? ordem.getStatus().getId() : null);
                });
    }
}
