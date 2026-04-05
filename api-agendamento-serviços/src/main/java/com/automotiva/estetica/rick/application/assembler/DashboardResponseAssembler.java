package com.automotiva.estetica.rick.application.assembler;

import com.automotiva.estetica.rick.application.dto.response.CancelamentoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoPeriodoResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoItemResponse;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoResponse;
import com.automotiva.estetica.rick.application.dto.response.ProximoAgendamentoResponse;
import com.automotiva.estetica.rick.domain.entity.CancelamentoResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoPeriodoResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoCategoriaResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoItemResumo;
import com.automotiva.estetica.rick.domain.entity.ProximoAgendamentoResumo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DashboardResponseAssembler {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ISO_LOCAL_DATE;

    public FaturamentoPeriodoResponse toFaturamentoPeriodoResponse(FaturamentoPeriodoResumo resumo) {
        return FaturamentoPeriodoResponse.builder().data(resumo.data())
                .faturamentoDiario(defaultValor(resumo.faturamentoDiario())).build();
    }

    public FaturamentoServicoResponse toFaturamentoServicoResponse(FaturamentoServicoCategoriaResumo resumo) {
        return FaturamentoServicoResponse.builder().categoria(defaultCategoria(resumo.categoria()))
                .servicos(resumo.servicos().stream().map(this::toServicoItemResponse).toList()).build();
    }

    public CancelamentoResponse toCancelamentoResponse(CancelamentoResumo resumo) {
        return CancelamentoResponse.builder().tipo(defaultTexto(resumo.tipo()))
                .quantidade(defaultQuantidade(resumo.quantidade())).build();
    }

    public ProximoAgendamentoResponse toProximoAgendamentoResponse(ProximoAgendamentoResumo resumo) {
        LocalDateTime dataAgendamento = resumo.dataAgendamento();
        return ProximoAgendamentoResponse.builder().ordemServicoId(resumo.ordemServicoId())
                .servico(defaultTexto(resumo.servico()))
                .hora(dataAgendamento != null ? dataAgendamento.toLocalTime().format(FORMATO_HORA) : null)
                .data(dataAgendamento != null ? dataAgendamento.toLocalDate().format(FORMATO_DATA) : null)
                .clienteNome(defaultTexto(resumo.clienteNome())).veiculoDescricao(construirVeiculoDescricao(resumo))
                .status(resumo.status()).build();
    }

    private FaturamentoServicoItemResponse toServicoItemResponse(FaturamentoServicoItemResumo resumo) {
        return FaturamentoServicoItemResponse.builder().servico(defaultServico(resumo.servico()))
                .quantidadeVendida(defaultQuantidade(resumo.quantidadeVendida()))
                .faturamento(defaultValor(resumo.faturamento())).build();
    }

    private String construirVeiculoDescricao(ProximoAgendamentoResumo resumo) {
        String marca = defaultTexto(resumo.veiculoMarca());
        String modelo = defaultTexto(resumo.veiculoModelo());
        String marcaModelo = (marca + " " + modelo).trim();
        if (!marcaModelo.isBlank()) {
            return marcaModelo;
        }
        return defaultTexto(resumo.veiculoPlaca());
    }

    private String defaultTexto(String valor) {
        return Optional.ofNullable(valor).orElse("").trim();
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
}
