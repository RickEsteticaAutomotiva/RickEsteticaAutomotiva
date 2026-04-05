package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.assembler.DashboardResponseAssembler;
import com.automotiva.estetica.rick.application.dto.response.CancelamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoPeriodoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoResponse;
import com.automotiva.estetica.rick.application.dto.response.FluxoCaixaResponse;
import com.automotiva.estetica.rick.application.dto.response.HomeResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.ProximoAgendamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensConcluidasMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.TicketMedioMensalResponse;
import com.automotiva.estetica.rick.domain.entity.FaturamentoMensalResumo;
import com.automotiva.estetica.rick.domain.entity.FluxoCaixaResumo;
import com.automotiva.estetica.rick.domain.entity.QtdOrdensConcluidasMensalResumo;
import com.automotiva.estetica.rick.domain.entity.QtdOrdensMensalResumo;
import com.automotiva.estetica.rick.domain.entity.TicketMedioMensalResumo;
import com.automotiva.estetica.rick.domain.enums.StatusOrdem;
import com.automotiva.estetica.rick.domain.gateway.DashboardGateway;
import com.automotiva.estetica.rick.domain.usecase.BuscarCancelamentosMensaisUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarFaturamentoMensalUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarFaturamentoPeriodoUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarFaturamentoServicosMensalUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarFluxoCaixaMensalUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarQtdOrdensConcluidasMensaisUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarQtdOrdensMensaisUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarTicketMedioMensalUseCase;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardApplicationService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final int MONETARIO_SCALE = 2;
    private static final ZoneId ZONE_ID_SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private final DashboardGateway dashboardGateway;
    private final BuscarFaturamentoMensalUseCase buscarFaturamentoMensalUseCase;
    private final BuscarQtdOrdensMensaisUseCase buscarQtdOrdensMensaisUseCase;
    private final BuscarQtdOrdensConcluidasMensaisUseCase buscarQtdOrdensConcluidasMensaisUseCase;
    private final BuscarTicketMedioMensalUseCase buscarTicketMedioMensalUseCase;
    private final BuscarFluxoCaixaMensalUseCase buscarFluxoCaixaMensalUseCase;
    private final BuscarCancelamentosMensaisUseCase buscarCancelamentosMensaisUseCase;
    private final BuscarFaturamentoPeriodoUseCase buscarFaturamentoPeriodoUseCase;
    private final BuscarFaturamentoServicosMensalUseCase buscarFaturamentoServicosMensalUseCase;
    private final DashboardResponseAssembler dashboardResponseAssembler;

    public FaturamentoResponse buscarFaturamentoTotal() {
        FaturamentoMensalResumo resumo = buscarFaturamentoMensalUseCase.execute();
        return FaturamentoResponse.builder().faturamentoAtual(defaultValor(resumo.faturamentoAtual()))
                .variacaoPercentual(defaultValor(resumo.variacaoPercentual())).build();
    }

    public QtdOrdensMensalResponse buscarQtdTotalAgendamentosMes() {
        QtdOrdensMensalResumo resumo = buscarQtdOrdensMensaisUseCase.execute();
        return QtdOrdensMensalResponse.builder().totalOrdens(Objects.requireNonNullElse(resumo.totalOrdens(), 0))
                .variacaoPercentual(defaultValor(resumo.variacaoPercentual())).build();
    }

    public QtdOrdensConcluidasMensalResponse buscarQtdOrdensConcluidasMes() {
        QtdOrdensConcluidasMensalResumo resumo = buscarQtdOrdensConcluidasMensaisUseCase.execute();
        return QtdOrdensConcluidasMensalResponse.builder()
                .totalOrdensConcluidas(Objects.requireNonNullElse(resumo.totalOrdensConcluidas(), 0))
                .variacaoPercentual(defaultValor(resumo.variacaoPercentual())).build();
    }

    public TicketMedioMensalResponse buscarTicketMedioMes() {
        TicketMedioMensalResumo resumo = buscarTicketMedioMensalUseCase.execute();
        return TicketMedioMensalResponse.builder()
                .totalTicketMedioMesAtual(defaultValor(resumo.totalTicketMedioMesAtual()))
                .variacaoPercentual(defaultValor(resumo.variacaoPercentual())).build();
    }

    public List<FaturamentoPeriodoResponse> buscarFaturamentoPeriodo() {
        return buscarFaturamentoPeriodoUseCase.execute().stream()
                .map(dashboardResponseAssembler::toFaturamentoPeriodoResponse).toList();
    }

    public List<FaturamentoServicoResponse> buscarFaturamentoServicos() {
        return buscarFaturamentoServicosMensalUseCase.execute().stream()
                .map(dashboardResponseAssembler::toFaturamentoServicoResponse).toList();
    }

    public FluxoCaixaResponse buscarFluxoCaixa() {
        FluxoCaixaResumo resumo = buscarFluxoCaixaMensalUseCase.execute();
        return FluxoCaixaResponse.builder().total(defaultValor(resumo.total())).lucro(defaultValor(resumo.lucro()))
                .custo(defaultValor(resumo.custo())).percentualLucro(defaultValor(resumo.percentualLucro()))
                .percentualCusto(defaultValor(resumo.percentualCusto())).build();
    }

    public List<CancelamentoResponse> buscarCancelamentos() {
        return buscarCancelamentosMensaisUseCase.execute().stream()
                .map(dashboardResponseAssembler::toCancelamentoResponse).toList();
    }

    public HomeResumoResponse buscarHomeResumo() {
        LocalDate hoje = LocalDate.now(ZONE_ID_SAO_PAULO);
        LocalDateTime inicioDia = hoje.atStartOfDay();
        LocalDateTime fimDia = hoje.atTime(23, 59, 59);
        LocalDateTime agora = LocalDateTime.now(ZONE_ID_SAO_PAULO);

        long agendamentosHoje = dashboardGateway.contarAgendamentosNoPeriodoExcetoStatus(inicioDia, fimDia,
                StatusOrdem.CANCELADO.getId());

        BigDecimal faturamentoEstimadoHoje = defaultValor(dashboardGateway
                .somarFaturamentoEstimadoNoPeriodoExcetoStatus(inicioDia, fimDia, StatusOrdem.CANCELADO.getId()))
                .setScale(MONETARIO_SCALE, RoundingMode.HALF_UP);

        BigDecimal ticketMedioEstimadoHoje = agendamentosHoje == 0
                ? ZERO.setScale(MONETARIO_SCALE, RoundingMode.HALF_UP)
                : faturamentoEstimadoHoje.divide(BigDecimal.valueOf(agendamentosHoje), MONETARIO_SCALE,
                        RoundingMode.HALF_UP);

        ProximoAgendamentoResponse proximoAgendamento = dashboardGateway
                .buscarProximoAgendamentoNoPeriodoExcetoStatus(agora, fimDia, StatusOrdem.CANCELADO.getId())
                .map(dashboardResponseAssembler::toProximoAgendamentoResponse).orElse(null);

        return HomeResumoResponse.builder().agendamentosHoje(agendamentosHoje)
                .faturamentoEstimadoHoje(faturamentoEstimadoHoje).ticketMedioEstimadoHoje(ticketMedioEstimadoHoje)
                .proximoAgendamento(proximoAgendamento).build();
    }

    private BigDecimal defaultValor(BigDecimal valor) {
        return Objects.requireNonNullElse(valor, ZERO);
    }

}
