package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoCategoriaResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoItemResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoResumo;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarFaturamentoServicosMensalUseCase {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final DashboardGateway dashboardGateway;

    public List<FaturamentoServicoCategoriaResumo> execute() {
        PeriodoMensal mesAtual = PeriodoMensalFactory.mesAtual();

        return dashboardGateway.buscarFaturamentoServicos(mesAtual.inicio(), mesAtual.fim()).stream()
                .collect(Collectors.groupingBy(dto -> defaultCategoria(dto.categoria()), LinkedHashMap::new,
                        Collectors.mapping(this::toServicoItemResponse, Collectors.toList())))
                .entrySet().stream()
                .map(entry -> new FaturamentoServicoCategoriaResumo(entry.getKey(), entry.getValue())).toList();
    }

    private FaturamentoServicoItemResumo toServicoItemResponse(FaturamentoServicoResumo dto) {
        return new FaturamentoServicoItemResumo(defaultServico(dto.servico()), defaultValor(dto.faturamento()),
                defaultQuantidade(dto.quantidadeVendida()));
    }

    private String defaultCategoria(String categoria) {
        return (categoria == null || categoria.isBlank()) ? "Sem categoria" : categoria;
    }

    private String defaultServico(String servico) {
        return (servico == null || servico.isBlank()) ? "Servico sem nome" : servico;
    }

    private Long defaultQuantidade(Long quantidade) {
        return quantidade == null ? 0L : quantidade;
    }

    private BigDecimal defaultValor(BigDecimal valor) {
        return valor == null ? ZERO : valor;
    }
}
