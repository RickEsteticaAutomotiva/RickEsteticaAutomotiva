package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.Utils.PeriodoMensal;
import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.DashboardMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.*;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ItemServicoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final DashboardMapper dashboardMapper;
    private final ItemServicoRepository itemServicoRepository;

    public FaturamentoResponseDto buscarFaturamentoTotal() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        BigDecimal faturamentoMesAtual =
                ordemServicoRepository.somarFaturamentoDoPeriodo(mesAtual.inicio(), mesAtual.fim());
        BigDecimal faturamentoMesAnterior =
                ordemServicoRepository.somarFaturamentoDoPeriodo(mesAtual.inicio(), mesAnterior.fim());

        BigDecimal variacaoMensal = calcularVariacaoMensal(faturamentoMesAtual, faturamentoMesAnterior);

        return dashboardMapper.paraFaturamentoResponseDto(faturamentoMesAtual, variacaoMensal);
    }

    public QtdOrdensServicoMensalResponseDto buscarQtdTotalAgendamentosMes() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        Integer totalOrdensMesAtual =
                ordemServicoRepository.buscarQtdOrdensServicoDoMes(mesAtual.inicio(), mesAtual.fim());
        Integer totalOrdensMesAnterior =
                ordemServicoRepository.buscarQtdOrdensServicoDoMes(mesAnterior.inicio(), mesAnterior.fim());

        BigDecimal variacaoMensalQtdOrdens = calcularVariacaoMensal(totalOrdensMesAtual, totalOrdensMesAnterior);

        return dashboardMapper.paraQtdOrdensMensalResponseDto(totalOrdensMesAtual, variacaoMensalQtdOrdens);
    }

    public QtdOrdensServicoConcluidasMensalResponseDto buscarQtdOrdensConcluidasMes() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        Integer totalOrdensConcluidasMesAtual =
                ordemServicoRepository.buscarQtdOrdensServicoConcluidasNoMes(mesAtual.inicio(), mesAtual.fim());

        Integer totalOrdensConcluidasMesAnterior =
                ordemServicoRepository.buscarQtdOrdensServicoConcluidasNoMes(mesAnterior.inicio(), mesAnterior.fim());

        BigDecimal variacaoMensalTotalOrdensConcluidas =
                calcularVariacaoMensal(totalOrdensConcluidasMesAtual, totalOrdensConcluidasMesAnterior);

        return dashboardMapper.paraQtdOrdensConcluidasMensalDto(
                totalOrdensConcluidasMesAtual, variacaoMensalTotalOrdensConcluidas);
    }

    public TicketMedioMensalResponseDto buscarTicketMedioMes() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        BigDecimal ticketMedioMesAtual =
                ordemServicoRepository.calcularTicketMedioDoMes(mesAtual.inicio(), mesAtual.fim());

        BigDecimal ticketMedioMesAnterior =
                ordemServicoRepository.calcularTicketMedioDoMes(mesAnterior.inicio(), mesAnterior.fim());

        BigDecimal variacaoMensalTicketMedio = calcularVariacaoMensal(ticketMedioMesAtual, ticketMedioMesAnterior);

        return dashboardMapper.paraTicketMedioMensalDto(ticketMedioMesAtual, variacaoMensalTicketMedio);
    }

    private <T extends Number> BigDecimal calcularVariacaoMensal(T valorMesAtual, T valorMesAnterior) {
        BigDecimal mesAtual = toBigDecimal(valorMesAtual);
        BigDecimal mesAnterior = toBigDecimal(valorMesAnterior);

        if (mesAnterior == null || mesAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return mesAtual.subtract(mesAnterior)
                .divide(mesAnterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal toBigDecimal(Number number) {
        if (number == null) return BigDecimal.ZERO;

        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }
        return new BigDecimal(number.toString());
    }

    public List<CategoriaDashboardDto> calcularFaturamentoPorServico() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        List<RegistroFaturamentoDto> registros = itemServicoRepository.buscarFaturamentoAgrupado(mesAtual.inicio(), mesAtual.fim());

        return registros.stream()
                .collect(Collectors.groupingBy(RegistroFaturamentoDto::getCategoriaNome))
                .entrySet().stream()
                .map(entry -> {
                    String categoria = entry.getKey();
                    List<RegistroFaturamentoDto> itens = entry.getValue();

                    double totalCategoria = itens.stream()
                            .map(RegistroFaturamentoDto::getTotalPreco)
                            .mapToDouble(BigDecimal::doubleValue)
                            .sum();

                    List<ServicoDashboardDto> servicos = itens.stream()
                            .map(item -> new ServicoDashboardDto(
                                    item.getServicoNome(),
                                    item.getTotalPreco().doubleValue()
                            ))
                            .toList();

                    return new CategoriaDashboardDto(categoria, totalCategoria, servicos);
                })
                .toList();
    }

    public FluxoCaixaDto fluxoCaixa() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();

        FaturamentoMensalDto registro = ordemServicoRepository
                .buscarFaturamentoPorMes(mesAtual.inicio(), mesAtual.fim());

        BigDecimal total = registro != null ? registro.getTotal() : BigDecimal.ZERO;
        BigDecimal lucro = registro != null ? registro.getLucro() : BigDecimal.ZERO;

        BigDecimal custo = total.subtract(lucro);

        BigDecimal percentualCusto = BigDecimal.ZERO;
        BigDecimal percentualLucro = BigDecimal.ZERO;
        if (total.compareTo(BigDecimal.ZERO) > 0) {
            percentualCusto = custo.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
            percentualLucro = lucro.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
        }

        return new FluxoCaixaDto(total, lucro, custo, percentualCusto, percentualLucro);
    }

    public List<Map<String, Object>> buscarCancelamentos() {
        List<Object[]> resultados = ordemServicoRepository.buscarCancelamentos();
        List<Map<String, Object>> cancelamentos = new ArrayList<>();

        for (Object[] row : resultados) {
            Map<String, Object> item = new HashMap<>();
            item.put("tipo", row[0]);
            item.put("quantidade", row[1]);
            cancelamentos.add(item);
        }

        return cancelamentos;
    }

    private PeriodoMensal getPeriodoMesAtual() {
        LocalDateTime inicio = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime fim = inicio.plusMonths(1).minusNanos(1);
        return new PeriodoMensal(inicio, fim);
    }

    private PeriodoMensal getPeriodoMesAnterior() {
        LocalDateTime inicio = LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay();
        LocalDateTime fim = inicio.plusMonths(1).minusNanos(1);
        return new PeriodoMensal(inicio, fim);
    }
}
