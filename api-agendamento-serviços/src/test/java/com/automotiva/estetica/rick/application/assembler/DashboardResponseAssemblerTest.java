package com.automotiva.estetica.rick.application.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.automotiva.estetica.rick.domain.entity.CancelamentoResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoPeriodoResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoCategoriaResumo;
import com.automotiva.estetica.rick.domain.entity.FaturamentoServicoItemResumo;
import com.automotiva.estetica.rick.domain.entity.ProximoAgendamentoResumo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de DashboardResponseAssembler")
class DashboardResponseAssemblerTest {

    private final DashboardResponseAssembler assembler = new DashboardResponseAssembler();

    @Test
    @DisplayName("deve aplicar valor zero no faturamento diario quando vier nulo")
    void toFaturamentoPeriodoResponse_deveAplicarZeroQuandoNulo() {
        var resumo = new FaturamentoPeriodoResumo(LocalDate.of(2026, 4, 1), null);

        var response = assembler.toFaturamentoPeriodoResponse(resumo);

        assertEquals(LocalDate.of(2026, 4, 1), response.getData());
        assertEquals(BigDecimal.ZERO, response.getFaturamentoDiario());
    }

    @Test
    @DisplayName("deve aplicar defaults de categoria e servico")
    void toFaturamentoServicoResponse_deveAplicarDefaults() {
        var item = new FaturamentoServicoItemResumo("  ", null, null);
        var resumo = new FaturamentoServicoCategoriaResumo("", List.of(item));

        var response = assembler.toFaturamentoServicoResponse(resumo);

        assertEquals("Sem categoria", response.getCategoria());
        assertEquals(1, response.getServicos().size());
        assertEquals("Servico sem nome", response.getServicos().getFirst().getServico());
        assertEquals(0L, response.getServicos().getFirst().getQuantidadeVendida());
        assertEquals(BigDecimal.ZERO, response.getServicos().getFirst().getFaturamento());
    }

    @Test
    @DisplayName("deve aplicar default para cancelamento com texto vazio")
    void toCancelamentoResponse_deveAplicarDefaultTexto() {
        var resumo = new CancelamentoResumo("   ", null);

        var response = assembler.toCancelamentoResponse(resumo);

        assertEquals("", response.getTipo());
        assertEquals(0L, response.getQuantidade());
    }

    @Test
    @DisplayName("deve formatar data hora e montar descricao de veiculo por marca e modelo")
    void toProximoAgendamentoResponse_deveFormatarDataHoraEVeiculo() {
        var resumo = new ProximoAgendamentoResumo(10L, " Lavagem ", LocalDateTime.of(2026, 4, 10, 14, 30), " Joao ",
                " Honda ", " Civic ", "ABC1D23", 2L);

        var response = assembler.toProximoAgendamentoResponse(resumo);

        assertEquals("14:30", response.getHora());
        assertEquals("2026-04-10", response.getData());
        assertEquals("Lavagem", response.getServico());
        assertEquals("Joao", response.getClienteNome());
        assertEquals("Honda Civic", response.getVeiculoDescricao());
        assertEquals(2L, response.getStatus());
    }

    @Test
    @DisplayName("deve usar placa quando marca e modelo estiverem vazios e data for nula")
    void toProximoAgendamentoResponse_deveUsarPlacaQuandoMarcaModeloVazios() {
        var resumo = new ProximoAgendamentoResumo(11L, null, null, null, "  ", "", "XYZ9K88", 1L);

        var response = assembler.toProximoAgendamentoResponse(resumo);

        assertNull(response.getHora());
        assertNull(response.getData());
        assertEquals("", response.getServico());
        assertEquals("", response.getClienteNome());
        assertEquals("XYZ9K88", response.getVeiculoDescricao());
    }
}
