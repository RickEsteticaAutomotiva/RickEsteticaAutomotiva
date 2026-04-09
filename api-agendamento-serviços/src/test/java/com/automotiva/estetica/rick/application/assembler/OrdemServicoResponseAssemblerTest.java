package com.automotiva.estetica.rick.application.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.entity.Status;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrdemServicoResponseAssemblerTest {

    private final OrdemServicoResponseAssembler assembler = new OrdemServicoResponseAssembler();

    @Test
    @DisplayName("Deve montar response com cliente, status e servicos")
    void toResponse_sucesso() {
        OrdemServico ordem = ordemComRelacionamentos();
        List<ItemServico> itens = itens(ordem);

        var response = assembler.toResponse(ordem, itens);

        assertEquals(10L, response.getId());
        assertNotNull(response.getCliente());
        assertEquals("Cliente Teste", response.getCliente().getNome());
        assertEquals(2, response.getServicos().size());
    }

    @Test
    @DisplayName("Deve somar valor total em detalhe para gestao")
    void toDetalheGestao_deveSomarValorTotal() {
        OrdemServico ordem = ordemComRelacionamentos();
        List<ItemServico> itens = itens(ordem);

        var detalhe = assembler.toDetalheGestao(ordem, itens);

        assertEquals(new BigDecimal("180"), detalhe.getValorTotal());
        assertEquals(2, detalhe.getServicos().size());
    }

    @Test
    @DisplayName("Deve mapear motivo de cancelamento quando presente")
    void toResponse_deveMapearMotivoCancelamento() {
        OrdemServico ordem = ordemComRelacionamentos();
        ordem.setMotivoCancelamento(com.automotiva.estetica.rick.domain.entity.MotivoCancelamento.builder().id(9L)
                .descricao("Cliente desistiu").build());

        var response = assembler.toResponse(ordem, itens(ordem));

        assertEquals(9L, response.getMotivo());
    }

    @Test
    @DisplayName("Deve retornar resumos nulos quando veiculo e status forem nulos")
    void toResponse_quandoSemVeiculoEStatus_deveRetornarCamposNulos() {
        OrdemServico ordem = OrdemServico.builder().id(99L).dataAgendamento(LocalDateTime.now()).status(null)
                .veiculo(null).build();

        var response = assembler.toResponse(ordem,
                List.of(ItemServico.builder().id(1L).servico(null).preco(null).build()));

        assertNull(response.getCliente());
        assertNull(response.getVeiculo());
        assertNull(response.getStatus());
        assertNull(response.getServicos().getFirst().getId());
        assertNull(response.getServicos().getFirst().getNome());
        assertNull(response.getServicos().getFirst().getPreco());
    }

    @Test
    @DisplayName("Deve ignorar precos nulos na soma de valor total")
    void toResumoGestao_quandoItensComPrecoNulo_deveSomarApenasValidos() {
        OrdemServico ordem = ordemComRelacionamentos();
        var itens = List.of(
                ItemServico.builder().id(1L).ordemServico(ordem)
                        .servico(Servico.builder().id(1L).nome("A").preco(BigDecimal.TEN).build()).preco(null).build(),
                ItemServico.builder().id(2L).ordemServico(ordem)
                        .servico(Servico.builder().id(2L).nome("B").preco(BigDecimal.ONE).build()).preco(BigDecimal.ONE)
                        .build());

        var resumo = assembler.toResumoGestao(ordem, itens);

        assertEquals(BigDecimal.ONE, resumo.getValorTotal());
    }

    private OrdemServico ordemComRelacionamentos() {
        Pessoa pessoa = Pessoa.builder().id(5L).nome("Cliente Teste").build();
        Veiculo veiculo = Veiculo.builder().id(1L).marca("VW").modelo("Golf").placa("ABC1234").pessoa(pessoa).build();
        Status status = Status.builder().id(2L).descricao("AGENDADO").build();

        return OrdemServico.builder().id(10L).dataAgendamento(LocalDateTime.now()).precoMinimo(BigDecimal.valueOf(120))
                .veiculo(veiculo).status(status).build();
    }

    private List<ItemServico> itens(OrdemServico ordem) {
        Servico lavagem = Servico.builder().id(1L).nome("Lavagem").preco(BigDecimal.valueOf(50)).build();
        Servico polimento = Servico.builder().id(2L).nome("Polimento").preco(BigDecimal.valueOf(110)).build();

        ItemServico item1 = ItemServico.builder().id(100L).ordemServico(ordem).servico(lavagem)
                .preco(BigDecimal.valueOf(60)).build();
        ItemServico item2 = ItemServico.builder().id(101L).ordemServico(ordem).servico(polimento)
                .preco(BigDecimal.valueOf(120)).build();

        return List.of(item1, item2);
    }
}
