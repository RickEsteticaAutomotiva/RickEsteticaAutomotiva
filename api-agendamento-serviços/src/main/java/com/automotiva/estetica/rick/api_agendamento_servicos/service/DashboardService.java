package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.DashboardMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.FaturamentoResponseDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.QtdOrdensServicoConcluidasMensalResponseDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.QtdOrdensServicoMensalResponseDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.TicketMedioMensalResponseDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final DashboardMapper dashboardMapper;

    private final LocalDateTime inicioMesAtual =
            LocalDate.now().withDayOfMonth(1).atStartOfDay();
    private final LocalDateTime fimMesAtual = inicioMesAtual.plusMonths(1).minusNanos(1);

    private final LocalDateTime inicioMesAnterior = inicioMesAtual.minusMonths(1);
    private final LocalDateTime fimMesAnterior = inicioMesAtual.minusNanos(1);

    public FaturamentoResponseDto buscarFaturamentoTotal() {
        BigDecimal faturamentoMesAtual = ordemServicoRepository.somarFaturamentoDoPeriodo(inicioMesAtual, fimMesAtual);
        BigDecimal faturamentoMesAnterior =
                ordemServicoRepository.somarFaturamentoDoPeriodo(inicioMesAnterior, fimMesAnterior);

        BigDecimal variacaoMensal = calcularVariacaoFaturamento(faturamentoMesAtual, faturamentoMesAnterior);

        return dashboardMapper.paraFaturamentoResponseDto(faturamentoMesAtual, variacaoMensal);
    }

    public QtdOrdensServicoMensalResponseDto buscarQtdTotalAgendamentosMes() {

        Integer totalOrdensMesAtual = ordemServicoRepository.findQtdOrdensServicoDoMes(inicioMesAtual, fimMesAtual);
        Integer totalOrdensMesAnterior =
                ordemServicoRepository.findQtdOrdensServicoDoMes(inicioMesAnterior, fimMesAnterior);

        BigDecimal variacaoMensalQtdOrdens = calcularVariacaoMensalOrdens(totalOrdensMesAtual, totalOrdensMesAnterior);

        return dashboardMapper.paraQtdOrdensMensalResponseDto(totalOrdensMesAtual, variacaoMensalQtdOrdens);
    }

    public QtdOrdensServicoConcluidasMensalResponseDto buscarQtdOrdensConcluidasMes() {
        Integer totalOrdensConcluidasMesAtual =
                ordemServicoRepository.findQtdOrdensServicoConcluidasNoMes(inicioMesAtual, fimMesAtual);

        Integer totalOrdensConcluidasMesAnterior =
                ordemServicoRepository.findQtdOrdensServicoConcluidasNoMes(inicioMesAnterior, fimMesAnterior);

        BigDecimal variacaoMensalTotalOrdensConcluidas =
                calcularVariacaoMensalOrdensConcluidas(totalOrdensConcluidasMesAtual, totalOrdensConcluidasMesAnterior);

        return dashboardMapper.paraQtdOrdensConcluidasMensalDto(
                totalOrdensConcluidasMesAtual, variacaoMensalTotalOrdensConcluidas);
    }

    public TicketMedioMensalResponseDto buscarTicketMedioMes() {
        BigDecimal ticketMedioMesAtual = ordemServicoRepository.calcularTicketMedioDoMes(inicioMesAtual, fimMesAtual);

        BigDecimal ticketMedioMesAnterior =
                ordemServicoRepository.calcularTicketMedioDoMes(inicioMesAnterior, fimMesAnterior);

        BigDecimal variacaoMensalTicketMedio =
                calcularVariacaoMensalTicketMedio(ticketMedioMesAtual, ticketMedioMesAnterior);

t         return dashboardMapper.paraTicketMedioMensalDto(ticketMedioMesAtual, variacaoMensalTicketMedio);
    }

    private BigDecimal calcularVariacaoFaturamento(BigDecimal faturamentoAtual, BigDecimal faturamentoAnterior) {
        if (faturamentoAnterior == null || faturamentoAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return faturamentoAtual
                .subtract(faturamentoAnterior)
                .divide(faturamentoAnterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calcularVariacaoMensalOrdens(Integer totalOrdensMesAtual, Integer totalOrdensMesAnterior) {
        if (totalOrdensMesAnterior == null || totalOrdensMesAnterior == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal atual = BigDecimal.valueOf(totalOrdensMesAtual);
        BigDecimal anterior = BigDecimal.valueOf(totalOrdensMesAnterior);

        return atual.subtract(anterior)
                .divide(anterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calcularVariacaoMensalOrdensConcluidas(
            Integer totalOrdensConcluidasMesAtual, Integer totalOrdensConcluidasMesAnterior) {
        if (totalOrdensConcluidasMesAnterior == null || totalOrdensConcluidasMesAnterior == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal atual = BigDecimal.valueOf(totalOrdensConcluidasMesAtual);
        BigDecimal anterior = BigDecimal.valueOf(totalOrdensConcluidasMesAnterior);

        return atual.subtract(anterior)
                .divide(anterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calcularVariacaoMensalTicketMedio(
            BigDecimal ticketMedioMesAtual, BigDecimal ticketMedioMesAnterior) {
        if (ticketMedioMesAnterior == null || ticketMedioMesAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return ticketMedioMesAtual
                .subtract(ticketMedioMesAnterior)
                .divide(ticketMedioMesAnterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
