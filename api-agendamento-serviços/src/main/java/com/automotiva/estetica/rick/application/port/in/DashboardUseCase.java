package com.automotiva.estetica.rick.application.port.in;

import com.automotiva.estetica.rick.application.dto.response.CancelamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoPeriodoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoResponse;
import com.automotiva.estetica.rick.application.dto.response.FluxoCaixaResponse;
import com.automotiva.estetica.rick.application.dto.response.HomeResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensConcluidasMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.QtdOrdensMensalResponse;
import com.automotiva.estetica.rick.application.dto.response.TicketMedioMensalResponse;
import java.util.List;

public interface DashboardUseCase {

    FaturamentoResponse buscarFaturamentoTotal();

    QtdOrdensMensalResponse buscarQtdTotalAgendamentosMes();

    QtdOrdensConcluidasMensalResponse buscarQtdOrdensConcluidasMes();

    TicketMedioMensalResponse buscarTicketMedioMes();

    List<FaturamentoPeriodoResponse> buscarFaturamentoPeriodo();

    List<FaturamentoServicoResponse> buscarFaturamentoServicos();

    FluxoCaixaResponse buscarFluxoCaixa();

    List<CancelamentoResponse> buscarCancelamentos();

    HomeResumoResponse buscarHomeResumo();
}
