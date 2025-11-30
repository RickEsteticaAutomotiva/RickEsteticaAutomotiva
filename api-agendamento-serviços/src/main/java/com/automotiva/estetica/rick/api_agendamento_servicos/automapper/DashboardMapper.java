package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.FaturamentoResponseDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.QtdOrdensServicoConcluidasMensalResponseDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.QtdOrdensServicoMensalResponseDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.TicketMedioMensalResponseDto;
import java.math.BigDecimal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

    FaturamentoResponseDto paraFaturamentoResponseDto(BigDecimal faturamentoAtual, BigDecimal variacaoPercentual);

    QtdOrdensServicoMensalResponseDto paraQtdOrdensMensalResponseDto(
            Integer totalOrdens, BigDecimal variacaoPercentual);

    QtdOrdensServicoConcluidasMensalResponseDto paraQtdOrdensConcluidasMensalDto(
            Integer totalOrdensConcluidas, BigDecimal variacaoPercentual);

    TicketMedioMensalResponseDto paraTicketMedioMensalDto(
            BigDecimal totalTicketMedioMesAtual, BigDecimal variacaoPercentual);
}
