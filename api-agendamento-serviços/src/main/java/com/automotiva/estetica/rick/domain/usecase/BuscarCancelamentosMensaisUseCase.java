package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.CancelamentoResumo;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarCancelamentosMensaisUseCase {

    private static final String TIPO_NAO_INFORMADO = "nao_informado";

    private final DashboardGateway dashboardGateway;

    public List<CancelamentoResumo> execute() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();

        return dashboardGateway.buscarCancelamentosPorMotivoDoPeriodo(mesAtual.inicio(), mesAtual.fim()).stream()
                .collect(Collectors.toMap(dto -> normalizarTipoCancelamento(dto.tipo()),
                        dto -> defaultQuantidade(dto.quantidade()), Long::sum, LinkedHashMap::new))
                .entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Long>, Long>comparing(Map.Entry::getValue).reversed()
                        .thenComparing(Map.Entry::getKey))
                .map(entry -> new CancelamentoResumo(entry.getKey(), entry.getValue())).toList();
    }

    private Long defaultQuantidade(Long quantidade) {
        return quantidade == null ? 0L : quantidade;
    }

    private String normalizarTipoCancelamento(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return TIPO_NAO_INFORMADO;
        }

        String semAcento = java.text.Normalizer
                .normalize(tipo.trim().toLowerCase(Locale.ROOT), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        String snakeCase = semAcento.replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
        return snakeCase.isBlank() ? TIPO_NAO_INFORMADO : snakeCase;
    }

    private PeriodoMensal getPeriodoMesAtual() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        return new PeriodoMensal(inicio.atStartOfDay(), LocalDateTime.now());
    }

    private record PeriodoMensal(LocalDateTime inicio, LocalDateTime fim) {
    }
}
