package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.*;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.DashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashService {
    private final DashRepository dashboardRepository;

    public List<CategoriaDashboardDto> calcularFaturamentoPorServico(Integer mes, Integer ano) {

        List<RegistroFaturamentoQuery> registros =
                dashboardRepository.buscarFaturamentoAgrupado(mes, ano);

        // Agrupar por categoria
        Map<String, List<RegistroFaturamentoQuery>> agrupado =
                registros.stream()
                        .collect(Collectors.groupingBy(RegistroFaturamentoQuery::getCategoriaNome));

        List<CategoriaDashboardDto> categorias = new ArrayList<>();

        agrupado.forEach((categoria, itens) -> {

            double totalCategoria = itens.stream()
                    .map(RegistroFaturamentoQuery::getTotalPreco)
                    .mapToDouble(BigDecimal::doubleValue)
                    .sum();

            List<ServicoDashboardDto> servicos = itens.stream()
                    .map(item -> new ServicoDashboardDto(
                            item.getServicoNome(),
                            item.getTotalPreco().doubleValue(),
//                            item.getLucro().doubleValue(),
                            totalCategoria == 0 ? 0 :
                                    item.getTotalPreco().doubleValue() / totalCategoria
                    ))
                    .toList();

            categorias.add(new CategoriaDashboardDto(
                    categoria,
                    totalCategoria,
                    servicos
            ));
        });

        return categorias;
    }

    public FluxoCaixaDto fluxoCaixa(int mes, int ano) {

        List<FaturamentoMensalQuery> registros =
                dashboardRepository.buscarFaturamentoPorMes(mes, ano);

        BigDecimal faturamentoTotal = registros.stream()
                .map(FaturamentoMensalQuery::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lucroTotal = registros.stream()
                .map(FaturamentoMensalQuery::getLucro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal custoTotal = faturamentoTotal.subtract(lucroTotal);

        // Percentuais
        BigDecimal pctLucro = BigDecimal.ZERO;
        BigDecimal pctCusto = BigDecimal.ZERO;

        if (faturamentoTotal.compareTo(BigDecimal.ZERO) > 0) {
            pctLucro = lucroTotal
                    .multiply(BigDecimal.valueOf(100))
                    .divide(faturamentoTotal, 2, RoundingMode.HALF_UP);

            pctCusto = custoTotal
                    .multiply(BigDecimal.valueOf(100))
                    .divide(faturamentoTotal, 2, RoundingMode.HALF_UP);
        }

        return new FluxoCaixaDto(
                faturamentoTotal,
                lucroTotal,
                custoTotal,
                pctLucro,
                pctCusto
        );

    }

}
