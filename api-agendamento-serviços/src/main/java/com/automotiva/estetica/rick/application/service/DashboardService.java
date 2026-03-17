package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.dto.response.CancelamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoDiarioDto;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoPeriodoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoDto;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoItemResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoResponse;
import com.automotiva.estetica.rick.application.dto.response.FluxoCaixaResponse;
import com.automotiva.estetica.rick.application.dto.response.HomeResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.ProximoAgendamentoDto;
import com.automotiva.estetica.rick.application.dto.response.ProximoAgendamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensConcluidasMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.TicketMedioMensalResponse;
import com.automotiva.estetica.rick.application.port.in.DashboardUseCase;
import com.automotiva.estetica.rick.application.port.out.OrdemServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.VariacaoPercentual;
import com.automotiva.estetica.rick.domain.enums.StatusOrdem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService implements DashboardUseCase {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final int PERCENTUAL_SCALE = 2;
    private static final int MONETARIO_SCALE = 2;
    private static final String TIPO_NAO_INFORMADO = "nao_informado";
    private static final ZoneId ZONE_ID_SAO_PAULO = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ISO_LOCAL_DATE;

    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @Override
    public FaturamentoResponse buscarFaturamentoTotal() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        BigDecimal atual = ordemServicoRepositoryPort.somarFaturamentoDoPeriodo(mesAtual.inicio(), mesAtual.fim());
        BigDecimal anterior = ordemServicoRepositoryPort.somarFaturamentoDoPeriodo(mesAnterior.inicio(),
                mesAnterior.fim());

        return FaturamentoResponse.builder().faturamentoAtual(atual)
                .variacaoPercentual(VariacaoPercentual.calcular(atual, anterior)).build();
    }

    @Override
    public QtdOrdensMensalResponse buscarQtdTotalAgendamentosMes() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        Integer atual = ordemServicoRepositoryPort.buscarQtdOrdensDoMes(mesAtual.inicio(), mesAtual.fim());
        Integer anterior = ordemServicoRepositoryPort.buscarQtdOrdensDoMes(mesAnterior.inicio(), mesAnterior.fim());

        return QtdOrdensMensalResponse.builder().totalOrdens(atual)
                .variacaoPercentual(VariacaoPercentual.calcular(atual, anterior)).build();
    }

    @Override
    public QtdOrdensConcluidasMensalResponse buscarQtdOrdensConcluidasMes() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        Integer atual = ordemServicoRepositoryPort.buscarQtdOrdensConcluidasNoMes(mesAtual.inicio(), mesAtual.fim());
        Integer anterior = ordemServicoRepositoryPort.buscarQtdOrdensConcluidasNoMes(mesAnterior.inicio(),
                mesAnterior.fim());

        return QtdOrdensConcluidasMensalResponse.builder().totalOrdensConcluidas(atual)
                .variacaoPercentual(VariacaoPercentual.calcular(atual, anterior)).build();
    }

    @Override
    public TicketMedioMensalResponse buscarTicketMedioMes() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        PeriodoMensal mesAnterior = getPeriodoMesAnterior();

        BigDecimal atual = ordemServicoRepositoryPort.calcularTicketMedioDoMes(mesAtual.inicio(), mesAtual.fim());
        BigDecimal anterior = ordemServicoRepositoryPort.calcularTicketMedioDoMes(mesAnterior.inicio(),
                mesAnterior.fim());

        return TicketMedioMensalResponse.builder().totalTicketMedioMesAtual(atual)
                .variacaoPercentual(VariacaoPercentual.calcular(atual, anterior)).build();
    }

    @Override
    public List<FaturamentoPeriodoResponse> buscarFaturamentoPeriodo() {
        LocalDateTime dataInicial = LocalDate.now().minusDays(30).atStartOfDay();
        List<FaturamentoDiarioDto> rows = ordemServicoRepositoryPort.buscarFaturamentoPorDia(dataInicial);
        return rows.stream()
                .map(
                        dto ->
                                FaturamentoPeriodoResponse.builder()
                                        .data(dto.dia())
                                        .faturamentoDiario(defaultValor(dto.totalDia()))
                                        .build())
                .toList();
    }

    @Override
    public List<FaturamentoServicoResponse> buscarFaturamentoServicos() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();
        return ordemServicoRepositoryPort.buscarFaturamentoServicos(mesAtual.inicio(), mesAtual.fim())
                .stream()
                .collect(
                        Collectors.groupingBy(
                                dto -> defaultCategoria(dto.categoria()),
                                LinkedHashMap::new,
                                Collectors.mapping(this::toServicoItemResponse, Collectors.toList())))
                .entrySet()
                .stream()
                .map(
                        entry ->
                                FaturamentoServicoResponse.builder()
                                        .categoria(entry.getKey())
                                        .servicos(entry.getValue())
                                        .build())
                .toList();
    }

    @Override
    public FluxoCaixaResponse buscarFluxoCaixa() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();

        BigDecimal lucro =
                defaultValor(
                        ordemServicoRepositoryPort.somarReceitaRecebidaDoPeriodo(
                                mesAtual.inicio(), mesAtual.fim()));
        BigDecimal custo =
                defaultValor(
                        ordemServicoRepositoryPort.somarCustoRealizadoDoPeriodo(
                                mesAtual.inicio(), mesAtual.fim()));

        BigDecimal total = lucro.add(custo);
        BigDecimal percentualLucro = calcularPercentual(lucro, total);
        BigDecimal percentualCusto =
                total.compareTo(ZERO) == 0
                        ? ZERO
                        : CEM.subtract(percentualLucro).setScale(PERCENTUAL_SCALE, RoundingMode.HALF_UP);

        return FluxoCaixaResponse.builder()
                .total(total)
                .lucro(lucro)
                .custo(custo)
                .percentualLucro(percentualLucro)
                .percentualCusto(percentualCusto)
                .build();
    }

    @Override
    public List<CancelamentoResponse> buscarCancelamentos() {
        PeriodoMensal mesAtual = getPeriodoMesAtual();

        return ordemServicoRepositoryPort
                .buscarCancelamentosPorMotivoDoPeriodo(mesAtual.inicio(), mesAtual.fim())
                .stream()
                .collect(
                        Collectors.toMap(
                                dto -> normalizarTipoCancelamento(dto.tipo()),
                                dto -> defaultQuantidade(dto.quantidade()),
                                Long::sum))
                .entrySet()
                .stream()
                .sorted(
                        Comparator.<Map.Entry<String, Long>, Long>comparing(Map.Entry::getValue)
                                .reversed()
                                .thenComparing(Map.Entry::getKey))
                .map(
                        entry ->
                                CancelamentoResponse.builder()
                                        .tipo(entry.getKey())
                                        .quantidade(entry.getValue())
                                        .build())
                .toList();
    }

    @Override
    public HomeResumoResponse buscarHomeResumo() {
        LocalDate hoje = LocalDate.now(ZONE_ID_SAO_PAULO);
        LocalDateTime inicioDia = hoje.atStartOfDay();
        LocalDateTime fimDia = hoje.atTime(23, 59, 59);
        LocalDateTime agora = LocalDateTime.now(ZONE_ID_SAO_PAULO);

        long agendamentosHoje =
                ordemServicoRepositoryPort.contarAgendamentosNoPeriodoExcetoStatus(
                        inicioDia, fimDia, StatusOrdem.CANCELADO.getId());

        BigDecimal faturamentoEstimadoHoje =
                defaultValor(
                                ordemServicoRepositoryPort
                                        .somarFaturamentoEstimadoNoPeriodoExcetoStatus(
                                                inicioDia,
                                                fimDia,
                                                StatusOrdem.CANCELADO.getId()))
                        .setScale(MONETARIO_SCALE, RoundingMode.HALF_UP);

        BigDecimal ticketMedioEstimadoHoje =
                agendamentosHoje == 0
                        ? ZERO.setScale(MONETARIO_SCALE, RoundingMode.HALF_UP)
                        : faturamentoEstimadoHoje.divide(
                                BigDecimal.valueOf(agendamentosHoje),
                                MONETARIO_SCALE,
                                RoundingMode.HALF_UP);

        ProximoAgendamentoResponse proximoAgendamento =
                ordemServicoRepositoryPort
                        .buscarProximoAgendamentoNoPeriodoExcetoStatus(
                                agora, fimDia, StatusOrdem.CANCELADO.getId())
                        .map(this::toProximoAgendamentoResponse)
                        .orElse(null);

        return HomeResumoResponse.builder()
                .agendamentosHoje(agendamentosHoje)
                .faturamentoEstimadoHoje(faturamentoEstimadoHoje)
                .ticketMedioEstimadoHoje(ticketMedioEstimadoHoje)
                .proximoAgendamento(proximoAgendamento)
                .build();
    }

    private ProximoAgendamentoResponse toProximoAgendamentoResponse(ProximoAgendamentoDto dto) {
        LocalDateTime dataAgendamento = dto.dataAgendamento();
        return ProximoAgendamentoResponse.builder()
                .ordemServicoId(dto.ordemServicoId())
                .servico(defaultTexto(dto.servico()))
                .hora(dataAgendamento != null ? dataAgendamento.toLocalTime().format(FORMATO_HORA) : null)
                .data(dataAgendamento != null ? dataAgendamento.toLocalDate().format(FORMATO_DATA) : null)
                .clienteNome(defaultTexto(dto.clienteNome()))
                .veiculoDescricao(construirVeiculoDescricao(dto))
                .status(dto.status())
                .build();
    }

    private String construirVeiculoDescricao(ProximoAgendamentoDto dto) {
        String marca = defaultTexto(dto.veiculoMarca());
        String modelo = defaultTexto(dto.veiculoModelo());
        String marcaModelo = (marca + " " + modelo).trim();
        if (!marcaModelo.isBlank()) {
            return marcaModelo;
        }
        return defaultTexto(dto.veiculoPlaca());
    }

    private String defaultTexto(String valor) {
        return Optional.ofNullable(valor).orElse("").trim();
    }

    private BigDecimal calcularPercentual(BigDecimal parcela, BigDecimal total) {
        if (total.compareTo(ZERO) == 0) {
            return ZERO;
        }
        return parcela.multiply(CEM).divide(total, PERCENTUAL_SCALE, RoundingMode.HALF_UP);
    }

    private FaturamentoServicoItemResponse toServicoItemResponse(FaturamentoServicoDto dto) {
        return FaturamentoServicoItemResponse.builder()
                .servico(defaultServico(dto.servico()))
                .quantidadeVendida(defaultQuantidade(dto.quantidadeVendida()))
                .faturamento(defaultValor(dto.faturamento()))
                .build();
    }

    private String defaultCategoria(String categoria) {
        return (categoria == null || categoria.isBlank()) ? "Sem categoria" : categoria;
    }

    private String defaultServico(String servico) {
        return (servico == null || servico.isBlank()) ? "Servico sem nome" : servico;
    }

    private Long defaultQuantidade(Long quantidadeVendida) {
        return Objects.requireNonNullElse(quantidadeVendida, 0L);
    }

    private BigDecimal defaultValor(BigDecimal valor) {
        return Objects.requireNonNullElse(valor, ZERO);
    }

    private String normalizarTipoCancelamento(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return TIPO_NAO_INFORMADO;
        }

        String semAcento =
                Normalizer.normalize(tipo.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                        .replaceAll("\\p{M}+", "");

        String snakeCase = semAcento.replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
        return snakeCase.isBlank() ? TIPO_NAO_INFORMADO : snakeCase;
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private PeriodoMensal getPeriodoMesAtual() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        return new PeriodoMensal(inicio.atStartOfDay(), LocalDateTime.now());
    }

    private PeriodoMensal getPeriodoMesAnterior() {
        LocalDate inicio = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate fim = inicio.plusMonths(1).minusDays(1);
        return new PeriodoMensal(inicio.atStartOfDay(), fim.atTime(23, 59, 59));
    }

    private record PeriodoMensal(LocalDateTime inicio, LocalDateTime fim) {
    }
}
