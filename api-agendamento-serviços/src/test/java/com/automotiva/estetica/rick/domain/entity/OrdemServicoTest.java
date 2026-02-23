package com.automotiva.estetica.rick.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrdemServicoTest {

    private OrdemServico ordemMock() {
        return OrdemServico.builder()
                .id(1L)
                .dataAgendamento(LocalDateTime.of(2026, 3, 10, 9, 0))
                .precoMinimo(BigDecimal.valueOf(200))
                .observacoes("Observação inicial")
                .status(Status.builder().id(1L).build())
                .build();
    }

    @Test
    @DisplayName("Deve atualizar apenas campos não nulos")
    void atualizar_apenasNaoNulos() {
        OrdemServico ordem = ordemMock();
        BigDecimal novoPreco = BigDecimal.valueOf(350);

        ordem.atualizar(null, novoPreco, null, null, null);

        assertEquals(LocalDateTime.of(2026, 3, 10, 9, 0), ordem.getDataAgendamento());
        assertEquals(novoPreco, ordem.getPrecoMinimo());
        assertEquals("Observação inicial", ordem.getObservacoes());
        assertEquals(1L, ordem.getStatus().getId());
    }

    @Test
    @DisplayName("Deve atualizar status corretamente")
    void atualizar_statusId_deveAtualizarStatus() {
        OrdemServico ordem = ordemMock();

        ordem.atualizar(null, null, null, 3L, null);

        assertEquals(3L, ordem.getStatus().getId());
    }

    @Test
    @DisplayName("Deve atualizar motivoCancelamento corretamente")
    void atualizar_motivoId_deveAtualizarMotivoCancelamento() {
        OrdemServico ordem = ordemMock();

        ordem.atualizar(null, null, null, null, 2L);

        assertNotNull(ordem.getMotivoCancelamento());
        assertEquals(2L, ordem.getMotivoCancelamento().getId());
    }

    @Test
    @DisplayName("criarItem deve retornar ItemServico com preco do servico e vinculo correto")
    void criarItem_sucesso() {
        OrdemServico ordem = ordemMock();
        Servico servico =
                Servico.builder().id(10L).preco(BigDecimal.valueOf(150)).build();

        ItemServico item = ordem.criarItem(servico);

        assertNotNull(item);
        assertEquals(servico, item.getServico());
        assertEquals(ordem, item.getOrdemServico());
        assertEquals(BigDecimal.valueOf(150), item.getPreco());
    }

    @Test
    @DisplayName("criarItem deve lançar IllegalArgumentException quando servico for nulo")
    void criarItem_servicoNulo_deveLancarExcecao() {
        OrdemServico ordem = ordemMock();

        assertThrows(IllegalArgumentException.class, () -> ordem.criarItem(null));
    }

    @Test
    @DisplayName("deveNotificarPorEmail deve retornar true para status 2")
    void deveNotificarPorEmail_status2_deveRetornarTrue() {
        OrdemServico ordem = ordemMock();
        ordem.setStatus(Status.builder().id(2L).build());

        assertTrue(ordem.deveNotificarPorEmail());
    }

    @Test
    @DisplayName("deveNotificarPorEmail deve retornar true para status 5")
    void deveNotificarPorEmail_status5_deveRetornarTrue() {
        OrdemServico ordem = ordemMock();
        ordem.setStatus(Status.builder().id(5L).build());

        assertTrue(ordem.deveNotificarPorEmail());
    }

    @Test
    @DisplayName("deveNotificarPorEmail deve retornar false para status 1")
    void deveNotificarPorEmail_status1_deveRetornarFalse() {
        OrdemServico ordem = ordemMock();
        ordem.setStatus(Status.builder().id(1L).build());

        assertFalse(ordem.deveNotificarPorEmail());
    }

    @Test
    @DisplayName("deveNotificarPorEmail deve retornar false quando status for nulo")
    void deveNotificarPorEmail_statusNulo_deveRetornarFalse() {
        OrdemServico ordem = ordemMock();
        ordem.setStatus(null);

        assertFalse(ordem.deveNotificarPorEmail());
    }

    @Test
    @DisplayName("atualizar com status CONCLUIDO (id=5) deve preencher dtConclusao automaticamente")
    void atualizar_statusConcluido_devePreencherDtConclusao() {
        OrdemServico ordem = ordemMock();
        assertNull(ordem.getDtConclusao());

        ordem.atualizar(null, null, null, 5L, null);

        assertNotNull(ordem.getDtConclusao());
        assertEquals(5L, ordem.getStatus().getId());
    }

    @Test
    @DisplayName("atualizar com status diferente de CONCLUIDO não deve alterar dtConclusao")
    void atualizar_statusNaoConcluido_naoDeveAlterarDtConclusao() {
        OrdemServico ordem = ordemMock();

        ordem.atualizar(null, null, null, 2L, null);

        assertNull(ordem.getDtConclusao());
        assertEquals(2L, ordem.getStatus().getId());
    }
}
