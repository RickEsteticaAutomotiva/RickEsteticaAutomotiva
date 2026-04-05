package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.automotiva.estetica.rick.infrastructure.entity.CategoriaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.ItemServicoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.MotivoCancelamentoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.PessoaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.ServicoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.StatusEntity;
import com.automotiva.estetica.rick.infrastructure.entity.VeiculoEntity;
import com.automotiva.estetica.rick.domain.entity.CancelamentoMotivoResumo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(DashboardGatewayImpl.class)
@DisplayName("Persistencia - DashboardGatewayImpl")
class DashboardGatewayImplIT {

    private static final Long STATUS_ANALISE_ID = 1L;
    private static final Long STATUS_CANCELADO_ID = 4L;
    private static final Long STATUS_CONCLUIDO_ID = 5L;

    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager em;

    @Autowired
    private DashboardGatewayImpl dashboardGateway;

    private VeiculoEntity veiculo;
    private StatusEntity statusAnalise;
    private StatusEntity statusConcluido;
    private StatusEntity statusCancelado;
    private ServicoEntity servicoVitrificacao;
    private ServicoEntity servicoPolimento;
    private MotivoCancelamentoEntity motivoCliente;
    private MotivoCancelamentoEntity motivoPeca;

    @BeforeEach
    void setUp() {
        garantirStatus(STATUS_ANALISE_ID, "Analise");
        garantirStatus(STATUS_CANCELADO_ID, "Cancelado");
        garantirStatus(STATUS_CONCLUIDO_ID, "Concluido");

        statusAnalise = em.find(StatusEntity.class, STATUS_ANALISE_ID);
        statusCancelado = em.find(StatusEntity.class, STATUS_CANCELADO_ID);
        statusConcluido = em.find(StatusEntity.class, STATUS_CONCLUIDO_ID);

        PessoaEntity pessoa = em.persistFlushFind(
                PessoaEntity.builder().nome("Pessoa Dashboard").cpf("32132132100").email("dashboard@email.com")
                        .telefone("11999990001").dataNascimento(LocalDate.of(1991, 1, 1)).senha("$2a$10$hash").build());

        veiculo = em.persistFlushFind(VeiculoEntity.builder().placa("DBG0001").modelo("Gol").marca("VW")
                .porte("Pequeno").cor("Prata").ano("2012").pessoa(pessoa).build());

        CategoriaEntity categoria = em.persistFlushFind(CategoriaEntity.builder().nome("Estetica").build());

        servicoVitrificacao = em.persistFlushFind(ServicoEntity.builder().nome("Vitrificacao").descricao("Protecao")
                .preco(BigDecimal.valueOf(250)).categoria(categoria).build());

        servicoPolimento = em.persistFlushFind(ServicoEntity.builder().nome("Polimento").descricao("Polimento")
                .preco(BigDecimal.valueOf(150)).categoria(categoria).build());

        motivoCliente = em
                .persistFlushFind(MotivoCancelamentoEntity.builder().descricao("Cliente desistiu").build());
        motivoPeca = em.persistFlushFind(MotivoCancelamentoEntity.builder().descricao("Falta peca").build());
    }

    @Test
    @DisplayName("deve calcular ticket medio apenas para ordens concluidas")
    void calcularTicketMedioDoMes_sucesso() {
        LocalDateTime inicio = LocalDateTime.of(2026, 5, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 5, 31, 23, 59);

        OrdemServicoEntity ordem1 = criarOrdem(LocalDateTime.of(2026, 5, 5, 10, 0), statusConcluido, 300);
        OrdemServicoEntity ordem2 = criarOrdem(LocalDateTime.of(2026, 5, 8, 10, 0), statusConcluido, 300);
        criarOrdem(LocalDateTime.of(2026, 5, 10, 10, 0), statusAnalise, 999);

        criarItem(ordem1, servicoVitrificacao, 200);
        criarItem(ordem1, servicoPolimento, 100);
        criarItem(ordem2, servicoPolimento, 300);

        BigDecimal ticketMedio = dashboardGateway.calcularTicketMedioDoMes(inicio, fim);

        assertThat(ticketMedio).isEqualByComparingTo("300.00");
    }

    @Test
    @DisplayName("deve somar receita recebida somente de concluidas")
    void somarReceitaRecebidaDoPeriodo_sucesso() {
        LocalDateTime inicio = LocalDateTime.of(2026, 7, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 7, 31, 23, 59);

        OrdemServicoEntity concluida = criarOrdem(LocalDateTime.of(2026, 7, 5, 10, 0), statusConcluido, 300);
        OrdemServicoEntity cancelada = criarOrdem(LocalDateTime.of(2026, 7, 6, 10, 0), statusCancelado, 999);

        criarItem(concluida, servicoVitrificacao, 180);
        criarItem(concluida, servicoPolimento, 120);
        criarItem(cancelada, servicoPolimento, 999);

        BigDecimal total = dashboardGateway.somarReceitaRecebidaDoPeriodo(inicio, fim);

        assertThat(total).isEqualByComparingTo("300.00");
    }

    @Test
    @DisplayName("deve agrupar cancelamentos por motivo")
    void buscarCancelamentosPorMotivoDoPeriodo_sucesso() {
        LocalDateTime inicio = LocalDateTime.of(2026, 9, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 9, 30, 23, 59);

        criarOrdem(LocalDateTime.of(2026, 9, 5, 10, 0), statusCancelado, 100, motivoCliente);
        criarOrdem(LocalDateTime.of(2026, 9, 6, 10, 0), statusCancelado, 100, motivoCliente);
        criarOrdem(LocalDateTime.of(2026, 9, 7, 10, 0), statusCancelado, 100, motivoPeca);
        criarOrdem(LocalDateTime.of(2026, 9, 8, 10, 0), statusCancelado, 100, null);

        List<CancelamentoMotivoResumo> resultado = dashboardGateway.buscarCancelamentosPorMotivoDoPeriodo(inicio, fim);

        assertThat(resultado).hasSize(3);
        assertThat(resultado).anySatisfy(item -> {
            assertThat(item.tipo()).isEqualTo("Cliente desistiu");
            assertThat(item.quantidade()).isEqualTo(2L);
        }).anySatisfy(item -> {
            assertThat(item.tipo()).isEqualTo("Falta peca");
            assertThat(item.quantidade()).isEqualTo(1L);
        }).anySatisfy(item -> {
            assertThat(item.tipo()).isNull();
            assertThat(item.quantidade()).isEqualTo(1L);
        });
    }

    @Test
    @DisplayName("deve buscar faturamento por servicos para concluidas")
    void buscarFaturamentoServicos_sucesso() {
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 6, 30, 23, 59);

        OrdemServicoEntity ordem1 = criarOrdem(LocalDateTime.of(2026, 6, 5, 10, 0), statusConcluido, 500);
        OrdemServicoEntity ordem2 = criarOrdem(LocalDateTime.of(2026, 6, 8, 10, 0), statusConcluido, 300);
        OrdemServicoEntity ordemAnalise = criarOrdem(LocalDateTime.of(2026, 6, 10, 10, 0), statusAnalise, 999);

        criarItem(ordem1, servicoVitrificacao, 250);
        criarItem(ordem2, servicoVitrificacao, 250);
        criarItem(ordem1, servicoPolimento, 150);
        criarItem(ordemAnalise, servicoPolimento, 999);

        var resultado = dashboardGateway.buscarFaturamentoServicos(inicio, fim);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.getFirst().servico()).isEqualTo("Vitrificacao");
        assertThat(resultado.getFirst().quantidadeVendida()).isEqualTo(2L);
        assertThat(resultado.getFirst().faturamento()).isEqualByComparingTo("500.00");
    }

    @Test
    @DisplayName("deve calcular resumo de agendamentos e proximo horario")
    void resumoHomeQueries_sucesso() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 10, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 11, 10, 23, 59);

        OrdemServicoEntity ordemValida = criarOrdem(LocalDateTime.of(2026, 11, 10, 15, 0), statusAnalise, 120);
        criarOrdem(LocalDateTime.of(2026, 11, 10, 10, 0), statusCancelado, 500);
        criarItem(ordemValida, servicoPolimento, 120);

        long quantidade = dashboardGateway.contarAgendamentosNoPeriodoExcetoStatus(inicio, fim, STATUS_CANCELADO_ID);
        BigDecimal estimado = dashboardGateway.somarFaturamentoEstimadoNoPeriodoExcetoStatus(inicio, fim,
                STATUS_CANCELADO_ID);
        var proximo = dashboardGateway.buscarProximoAgendamentoNoPeriodoExcetoStatus(
                LocalDateTime.of(2026, 11, 10, 12, 0), fim, STATUS_CANCELADO_ID);

        assertThat(quantidade).isEqualTo(1L);
        assertThat(estimado).isEqualByComparingTo("120.00");
        assertThat(proximo).isPresent();
        assertThat(proximo.get().ordemServicoId()).isEqualTo(ordemValida.getId());
        assertThat(proximo.get().servico()).isEqualTo("Polimento");
    }

    private void garantirStatus(Long id, String descricao) {
        em.getEntityManager().createNativeQuery("insert into status (id, descricao) values (:id, :descricao)")
                .setParameter("id", id).setParameter("descricao", descricao).executeUpdate();
    }

    private OrdemServicoEntity criarOrdem(LocalDateTime data, StatusEntity status, int precoMinimo) {
        return criarOrdem(data, status, precoMinimo, null);
    }

    private OrdemServicoEntity criarOrdem(LocalDateTime data, StatusEntity status, int precoMinimo,
            MotivoCancelamentoEntity motivo) {
        return em.persistFlushFind(
                OrdemServicoEntity.builder().dataAgendamento(data).precoMinimo(BigDecimal.valueOf(precoMinimo))
                        .veiculo(veiculo).status(status).motivoCancelamento(motivo).observacoes("Teste").build());
    }

    private void criarItem(OrdemServicoEntity ordem, ServicoEntity servico, int preco) {
        em.persistFlushFind(ItemServicoEntity.builder().ordemServico(ordem).servico(servico)
                .preco(BigDecimal.valueOf(preco)).build());
    }
}
