package com.automotiva.estetica.rick.infrastructure.repository.ordemservico;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.automotiva.estetica.rick.infrastructure.entity.MotivoCancelamentoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.PessoaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.StatusEntity;
import com.automotiva.estetica.rick.infrastructure.entity.VeiculoEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class OrdemServicoSpecificationTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private OrdemServicoRepository repository;

    private StatusEntity statusAnalise;
    private StatusEntity statusCancelado;
    private MotivoCancelamentoEntity motivo;

    @BeforeEach
    void setUp() {
        statusAnalise = em.persistFlushFind(StatusEntity.builder().descricao("Analise").build());
        statusCancelado = em.persistFlushFind(StatusEntity.builder().descricao("Cancelado").build());
        motivo = em.persistFlushFind(MotivoCancelamentoEntity.builder().descricao("Cliente desistiu").build());

        PessoaEntity pessoa = em.persistFlushFind(PessoaEntity.builder().nome("Pessoa Teste").cpf("00011122233")
                .email("pessoa.spec@teste.com").telefone("11999999999").dataNascimento(LocalDate.of(1990, 1, 1))
                .senha("123").build());

        VeiculoEntity v1 = em.persistFlushFind(VeiculoEntity.builder().placa("AAA1234").modelo("Gol").marca("VW")
                .porte("P").cor("Prata").ano("2012").pessoa(pessoa).build());
        VeiculoEntity v2 = em.persistFlushFind(VeiculoEntity.builder().placa("BBB0001").modelo("Civic")
                .marca("Honda").porte("M").cor("Preto").ano("2018").pessoa(pessoa).build());

        em.persistFlushFind(OrdemServicoEntity.builder().dataAgendamento(LocalDateTime.of(2026, 4, 1, 10, 0))
                .precoMinimo(BigDecimal.valueOf(100)).status(statusAnalise).veiculo(v1).observacoes("polimento geral")
                .build());

        em.persistFlushFind(OrdemServicoEntity.builder().dataAgendamento(LocalDateTime.of(2026, 4, 2, 10, 0))
                .precoMinimo(BigDecimal.valueOf(200)).status(statusCancelado).veiculo(v2)
                .observacoes("cancelada por cliente").motivoCancelamento(motivo).build());
    }

    @Test
    void filtroUnico_quandoNulo_deveRetornarTodos() {
        long total = repository.findAll(OrdemServicoSpecification.filtroUnico(null)).size();
        assertEquals(2L, total);
    }

    @Test
    void filtroUnico_quandoBlank_deveRetornarTodos() {
        long total = repository.findAll(OrdemServicoSpecification.filtroUnico("   ")).size();
        assertEquals(2L, total);
    }

    @Test
    void filtroUnico_deveFiltrarPorPlacaStatusMotivoOuObservacao() {
        assertEquals(1, repository.findAll(OrdemServicoSpecification.filtroUnico("aaa1234")).size());
        assertEquals(1, repository.findAll(OrdemServicoSpecification.filtroUnico("cancelado")).size());
        assertEquals(1, repository.findAll(OrdemServicoSpecification.filtroUnico("cliente desistiu")).size());
        assertEquals(1, repository.findAll(OrdemServicoSpecification.filtroUnico("polimento")).size());
    }

    @Test
    void filtroGestao_deveAplicarCombinacaoDeStatusPeriodoEFiltro() {
        var spec = OrdemServicoSpecification.filtroGestao("civic", statusCancelado.getId(),
                LocalDateTime.of(2026, 4, 2, 0, 0), LocalDateTime.of(2026, 4, 2, 23, 59));

        long total = repository.findAll(spec).size();

        assertEquals(1L, total);
    }

    @Test
    void filtroGestao_quandoSemFiltros_deveRetornarTodos() {
        long total = repository.findAll(OrdemServicoSpecification.filtroGestao(null, null, null, null)).size();
        assertEquals(2L, total);
    }
}

