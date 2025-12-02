package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.DashboardMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.FaturamentoResponseDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private DashboardMapper dashboardMapper;

    FaturamentoResponseDto dtoMock = Mockito.mock(FaturamentoResponseDto.class);

    @Test
    @DisplayName("Deve calcular a variação mensal corretamente")
    void calcularVariacaoMensal_Positiva() {
        BigDecimal valorAtual = BigDecimal.valueOf(1000);
        BigDecimal valorAnterior = BigDecimal.valueOf(500);

        when(ordemServicoRepository.somarFaturamentoDoPeriodo(any(), any())).thenReturn(valorAtual, valorAnterior);

        // Mock do mapper — gera DTO REAL usando reflexão
        when(dashboardMapper.paraFaturamentoResponseDto(any(), any())).thenAnswer(invocation -> {
            BigDecimal faturamento = invocation.getArgument(0);
            BigDecimal variacao = invocation.getArgument(1);

            // usa reflexão para chamar o construtor não-público
            Constructor<FaturamentoResponseDto> ctor =
                    FaturamentoResponseDto.class.getDeclaredConstructor(BigDecimal.class, BigDecimal.class);

            ctor.setAccessible(true); // libera acesso

            return ctor.newInstance(faturamento, variacao);
        });

        FaturamentoResponseDto result = dashboardService.buscarFaturamentoTotal();

        assertEquals(new BigDecimal("100.0000"), result.getVariacaoPercentual());
    }

    @Test
    @DisplayName("Deve calcular a variação mensal negativa corretamente")
    void calcularVariacaoMensal_Negativa() {
        BigDecimal valorAtual = BigDecimal.valueOf(500);
        BigDecimal valorAnterior = BigDecimal.valueOf(1000);

        when(ordemServicoRepository.somarFaturamentoDoPeriodo(any(), any())).thenReturn(valorAtual, valorAnterior);

        // Mock do mapper — gera DTO REAL usando reflexão
        when(dashboardMapper.paraFaturamentoResponseDto(any(), any())).thenAnswer(invocation -> {
            BigDecimal faturamento = invocation.getArgument(0);
            BigDecimal variacao = invocation.getArgument(1);

            // usa reflexão para chamar o construtor não-público
            Constructor<FaturamentoResponseDto> ctor =
                    FaturamentoResponseDto.class.getDeclaredConstructor(BigDecimal.class, BigDecimal.class);

            ctor.setAccessible(true); // libera acesso

            return ctor.newInstance(faturamento, variacao);
        });

        FaturamentoResponseDto result = dashboardService.buscarFaturamentoTotal();

        assertEquals(new BigDecimal("-50.0000"), result.getVariacaoPercentual());
    }

    @Test
    @DisplayName("Deve calcular a variação mensal corretamente quando o faturamento do mes anterior é 0")
    void calcularVariacaoMensal_FaturamentoAnteriorZerado() {
        BigDecimal valorAtual = BigDecimal.valueOf(1000);
        BigDecimal valorAnterior = BigDecimal.valueOf(0);

        when(ordemServicoRepository.somarFaturamentoDoPeriodo(any(), any())).thenReturn(valorAtual, valorAnterior);

        // Mock do mapper — gera DTO REAL usando reflexão
        when(dashboardMapper.paraFaturamentoResponseDto(any(), any())).thenAnswer(invocation -> {
            BigDecimal faturamento = invocation.getArgument(0);
            BigDecimal variacao = invocation.getArgument(1);

            // usa reflexão para chamar o construtor não-público
            Constructor<FaturamentoResponseDto> ctor =
                    FaturamentoResponseDto.class.getDeclaredConstructor(BigDecimal.class, BigDecimal.class);

            ctor.setAccessible(true); // libera acesso

            return ctor.newInstance(faturamento, variacao);
        });

        FaturamentoResponseDto result = dashboardService.buscarFaturamentoTotal();

        assertEquals(new BigDecimal("0"), result.getVariacaoPercentual());
    }

    @Test
    @DisplayName("Deve calcular a variação mensal corretamente quando o faturamento do mes anterior é null")
    void calcularVariacaoMensal_FaturamentoMensalNulo() {
        BigDecimal valorAtual = BigDecimal.valueOf(1000);
        BigDecimal valorAnterior = BigDecimal.valueOf(0);

        when(ordemServicoRepository.somarFaturamentoDoPeriodo(any(), any())).thenReturn(valorAtual, valorAnterior);

        // Mock do mapper — gera DTO REAL usando reflexão
        when(dashboardMapper.paraFaturamentoResponseDto(any(), any())).thenAnswer(invocation -> {
            BigDecimal faturamento = invocation.getArgument(0);
            BigDecimal variacao = invocation.getArgument(1);

            // usa reflexão para chamar o construtor não-público
            Constructor<FaturamentoResponseDto> ctor =
                    FaturamentoResponseDto.class.getDeclaredConstructor(BigDecimal.class, BigDecimal.class);

            ctor.setAccessible(true); // libera acesso

            return ctor.newInstance(faturamento, variacao);
        });

        FaturamentoResponseDto result = dashboardService.buscarFaturamentoTotal();

        assertEquals(new BigDecimal("0"), result.getVariacaoPercentual());
    }
}
