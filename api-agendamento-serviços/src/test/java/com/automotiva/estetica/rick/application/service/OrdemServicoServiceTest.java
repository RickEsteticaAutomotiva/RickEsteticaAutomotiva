package com.automotiva.estetica.rick.application.service;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResponse;
import com.automotiva.estetica.rick.application.port.in.CarrinhoUseCase;
import com.automotiva.estetica.rick.application.port.out.EmailPort;
import com.automotiva.estetica.rick.application.port.out.ItemServicoRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.OrdemServicoRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.ServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Status;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock private OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    @Mock private ItemServicoRepositoryPort itemServicoRepositoryPort;

    @Mock private ServicoRepositoryPort servicoRepositoryPort;

    @Mock private CarrinhoUseCase carrinhoUseCase;

    @Mock private EmailPort emailPort;

    @InjectMocks private OrdemServicoService ordemServicoService;

    private OrdemServico ordemMock() {
        Veiculo veiculo = Veiculo.builder().id(1L).build();
        Status status = Status.builder().id(1L).build();
        return OrdemServico.builder()
                .id(10L)
                .dataAgendamento(LocalDateTime.now())
                .precoMinimo(BigDecimal.valueOf(100))
                .veiculo(veiculo)
                .status(status)
                .build();
    }

    @Test
    @DisplayName("Deve retornar lista de ordens de serviço por usuário")
    void buscarPorUsuarioId_sucesso() {
        OrdemServico ordem = ordemMock();

        when(ordemServicoRepositoryPort.buscarPorVeiculoPessoaId(1L)).thenReturn(List.of(ordem));
        when(itemServicoRepositoryPort.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        List<OrdemServicoResponse> resultado = ordemServicoService.buscarPorUsuarioId(1L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.getFirst().getId());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não possui ordens de serviço")
    void buscarPorUsuarioId_listaVazia_deveRetornarListaVazia() {
        when(ordemServicoRepositoryPort.buscarPorVeiculoPessoaId(1L)).thenReturn(emptyList());

        List<OrdemServicoResponse> resultado = ordemServicoService.buscarPorUsuarioId(1L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ordem por ID inexistente")
    void buscarPorId_inexistente_deveLancarExcecao() {
        when(ordemServicoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class, () -> ordemServicoService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve retornar ordem de serviço por ID com sucesso")
    void buscarPorId_sucesso() {
        OrdemServico ordem = ordemMock();

        when(ordemServicoRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(ordem));
        when(itemServicoRepositoryPort.buscarPorOrdemServicoId(10L)).thenReturn(emptyList());

        OrdemServicoResponse response = ordemServicoService.buscarPorId(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
    }

    @Test
    @DisplayName("Deve retornar página de ordens ao buscar todos")
    void buscarTodos_sucesso() {
        Page<OrdemServico> page = new PageImpl<>(List.of(ordemMock()));

        when(ordemServicoRepositoryPort.buscarTodos(isNull(), any(Pageable.class)))
                .thenReturn(page);

        PageRequest req = new PageRequest();
        req.setPagina(0);
        req.setTamanho(10);

        Page<OrdemServicoResponse> resultado = ordemServicoService.buscarTodos(req);

        assertEquals(1, resultado.getTotalElements());
    }
}
