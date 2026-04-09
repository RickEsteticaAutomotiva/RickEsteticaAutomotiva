package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ErroLogGateway;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cobertura de use cases restantes")
class UseCasesRestantesDelegationTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ItemServicoGateway itemServicoGateway;

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private ErroLogGateway erroLogGateway;

    @Test
    void atualizarVeiculo_deveAtualizarESalvar() {
        AtualizarVeiculoUseCase useCase = new AtualizarVeiculoUseCase(veiculoGateway);
        Veiculo veiculo = Veiculo.builder().id(1L).placa("AAA1111").modelo("Uno").marca("Fiat").build();
        when(veiculoGateway.buscarPorId(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoGateway.salvar(veiculo)).thenReturn(veiculo);

        Veiculo resultado = useCase.execute(1L, "BBB2222", "Mobi", "Fiat", "Hatch", "Prata", "2024");

        assertEquals("BBB2222", resultado.getPlaca());
        assertEquals("Mobi", resultado.getModelo());
        verify(veiculoGateway).salvar(veiculo);
    }

    @Test
    void atualizarVeiculo_quandoNaoExiste_deveLancar() {
        AtualizarVeiculoUseCase useCase = new AtualizarVeiculoUseCase(veiculoGateway);
        when(veiculoGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(99L, "X", "Y", "Z", "P", "C", "A"));
    }

    @Test
    void removerServicoOrdem_deveRemoverItemERetornarOrdem() {
        RemoverServicoOrdemServicoUseCase useCase = new RemoverServicoOrdemServicoUseCase(ordemServicoGateway,
                itemServicoGateway);
        OrdemServico ordem = OrdemServico.builder().id(10L).build();
        ItemServico item = ItemServico.builder().id(50L).build();
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.of(ordem));
        when(itemServicoGateway.buscarPorOrdemServicoIdEServicoId(10L, 3L)).thenReturn(Optional.of(item));

        OrdemServico resultado = useCase.execute(10L, 3L);

        assertEquals(10L, resultado.getId());
        verify(itemServicoGateway).removerPorId(50L);
    }

    @Test
    void removerServicoOrdem_quandoOrdemNaoExiste_deveLancar() {
        RemoverServicoOrdemServicoUseCase useCase = new RemoverServicoOrdemServicoUseCase(ordemServicoGateway,
                itemServicoGateway);
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(10L, 3L));
    }

    @Test
    void removerServicoOrdem_quandoItemNaoExiste_deveLancar() {
        RemoverServicoOrdemServicoUseCase useCase = new RemoverServicoOrdemServicoUseCase(ordemServicoGateway,
                itemServicoGateway);
        when(ordemServicoGateway.buscarPorIdComDetalhes(10L))
                .thenReturn(Optional.of(OrdemServico.builder().id(10L).build()));
        when(itemServicoGateway.buscarPorOrdemServicoIdEServicoId(10L, 3L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(10L, 3L));
    }

    @Test
    void listarItensServicoPorOrdem_deveDelegar() {
        ListarItensServicoPorOrdemUseCase useCase = new ListarItensServicoPorOrdemUseCase(itemServicoGateway);
        List<ItemServico> itens = List.of(ItemServico.builder().id(1L).build());
        when(itemServicoGateway.buscarPorOrdemServicoId(20L)).thenReturn(itens);

        List<ItemServico> resultado = useCase.execute(20L);

        assertEquals(1, resultado.size());
        verify(itemServicoGateway).buscarPorOrdemServicoId(20L);
    }

    @Test
    void listarOrdensServico_deveDelegar() {
        ListarOrdensServicoUseCase useCase = new ListarOrdensServicoUseCase(ordemServicoGateway);
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(OrdemServico.builder().id(1L).build()));
        when(ordemServicoGateway.buscarTodos("abc", pageable)).thenReturn(page);

        var resultado = useCase.execute("abc", pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void buscarOrdensPorUsuario_deveDelegar() {
        BuscarOrdensServicoPorUsuarioUseCase useCase = new BuscarOrdensServicoPorUsuarioUseCase(ordemServicoGateway);
        when(ordemServicoGateway.buscarPorVeiculoPessoaId(7L))
                .thenReturn(List.of(OrdemServico.builder().id(7L).build()));

        List<OrdemServico> resultado = useCase.execute(7L);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarOrdemPorId_deveRetornarQuandoExistir() {
        BuscarOrdemServicoPorIdUseCase useCase = new BuscarOrdemServicoPorIdUseCase(ordemServicoGateway);
        OrdemServico ordem = OrdemServico.builder().id(31L).build();
        when(ordemServicoGateway.buscarPorId(31L)).thenReturn(Optional.of(ordem));

        OrdemServico resultado = useCase.execute(31L);

        assertEquals(31L, resultado.getId());
    }

    @Test
    void buscarOrdemPorId_quandoNaoExiste_deveLancar() {
        BuscarOrdemServicoPorIdUseCase useCase = new BuscarOrdemServicoPorIdUseCase(ordemServicoGateway);
        when(ordemServicoGateway.buscarPorId(31L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(31L));
    }

    @Test
    void buscarOrdemComDetalhes_deveRetornarQuandoExistir() {
        BuscarOrdemServicoComDetalhesUseCase useCase = new BuscarOrdemServicoComDetalhesUseCase(ordemServicoGateway);
        OrdemServico ordem = OrdemServico.builder().id(41L).build();
        when(ordemServicoGateway.buscarPorIdComDetalhes(41L)).thenReturn(Optional.of(ordem));

        OrdemServico resultado = useCase.execute(41L);

        assertEquals(41L, resultado.getId());
    }

    @Test
    void buscarOrdemComDetalhes_quandoNaoExiste_deveLancar() {
        BuscarOrdemServicoComDetalhesUseCase useCase = new BuscarOrdemServicoComDetalhesUseCase(ordemServicoGateway);
        when(ordemServicoGateway.buscarPorIdComDetalhes(41L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(41L));
    }

    @Test
    void buscarItemServicoPorId_deveRetornarQuandoExistir() {
        BuscarItemServicoPorIdUseCase useCase = new BuscarItemServicoPorIdUseCase(itemServicoGateway);
        ItemServico item = ItemServico.builder().id(70L).build();
        when(itemServicoGateway.buscarPorId(70L)).thenReturn(Optional.of(item));

        ItemServico resultado = useCase.execute(70L);

        assertEquals(70L, resultado.getId());
    }

    @Test
    void buscarItemServicoPorId_quandoNaoExiste_deveLancar() {
        BuscarItemServicoPorIdUseCase useCase = new BuscarItemServicoPorIdUseCase(itemServicoGateway);
        when(itemServicoGateway.buscarPorId(70L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(70L));
    }

    @Test
    void buscarErroLogPorId_deveRetornarQuandoExistir() {
        BuscarErroLogPorIdUseCase useCase = new BuscarErroLogPorIdUseCase(erroLogGateway);
        ErroLog erroLog = ErroLog.builder().id(80L).build();
        when(erroLogGateway.buscarPorId(80L)).thenReturn(Optional.of(erroLog));

        ErroLog resultado = useCase.execute(80L);

        assertEquals(80L, resultado.getId());
    }

    @Test
    void buscarErroLogPorId_quandoNaoExiste_deveLancar() {
        BuscarErroLogPorIdUseCase useCase = new BuscarErroLogPorIdUseCase(erroLogGateway);
        when(erroLogGateway.buscarPorId(80L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(80L));
    }

    @Test
    void buscarErroLogsPaginados_deveDelegar() {
        BuscarErroLogsPaginadosUseCase useCase = new BuscarErroLogsPaginadosUseCase(erroLogGateway);
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(ErroLog.builder().id(1L).build()));
        when(erroLogGateway.buscarTodos(pageable)).thenReturn(page);

        var resultado = useCase.execute(pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(erroLogGateway).buscarTodos(pageable);
    }

    @Test
    void buscarErroLogsComFiltros_deveDelegar() {
        BuscarErroLogsComFiltrosUseCase useCase = new BuscarErroLogsComFiltrosUseCase(erroLogGateway);
        var pageable = PageRequest.of(0, 5);
        var de = LocalDateTime.now().minusDays(1);
        var ate = LocalDateTime.now();
        var page = new PageImpl<>(List.of(ErroLog.builder().id(2L).build()));
        when(erroLogGateway.buscarComFiltros("RuntimeException", 500, "admin@test.com", de, ate, pageable))
                .thenReturn(page);

        var resultado = useCase.execute("RuntimeException", 500, "admin@test.com", de, ate, pageable);

        assertTrue(resultado.hasContent());
    }

    @Test
    void registrarErroLog_deveDelegar() {
        RegistrarErroLogUseCase useCase = new RegistrarErroLogUseCase(erroLogGateway);
        ErroLog erroLog = ErroLog.builder().id(3L).mensagem("falha").build();
        when(erroLogGateway.salvar(erroLog)).thenReturn(erroLog);

        ErroLog resultado = useCase.execute(erroLog);

        assertEquals(3L, resultado.getId());
        verify(erroLogGateway).salvar(erroLog);
    }

    @Test
    void purgarErroLogsAntigos_deveDelegar() {
        PurgarErroLogsAntigosUseCase useCase = new PurgarErroLogsAntigosUseCase(erroLogGateway);
        LocalDateTime limite = LocalDateTime.now().minusDays(30);

        useCase.execute(limite);

        verify(erroLogGateway).deletarAnterioresA(limite);
    }
}
