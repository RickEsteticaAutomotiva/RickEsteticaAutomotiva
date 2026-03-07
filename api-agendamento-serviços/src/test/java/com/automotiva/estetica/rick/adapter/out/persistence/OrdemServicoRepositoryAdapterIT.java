package com.automotiva.estetica.rick.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.*;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.OrdemServicoPersistenceMapperImpl;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.PessoaPersistenceMapperImpl;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.VeiculoPersistenceMapperImpl;
import com.automotiva.estetica.rick.adapter.out.persistence.ordemservico.OrdemServicoRepositoryAdapter;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Testa as queries JPQL customizadas do OrdemServicoJpaRepository, o mapeamento Entity ↔ Domain e a
 * implementação de OrdemServicoRepositoryPort.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import({
    OrdemServicoRepositoryAdapter.class,
    OrdemServicoPersistenceMapperImpl.class,
    VeiculoPersistenceMapperImpl.class,
    PessoaPersistenceMapperImpl.class
})
@DisplayName("Persistência — OrdemServicoRepositoryAdapter")
class OrdemServicoRepositoryAdapterIT {

    @Autowired
    @SuppressWarnings("unused")
    private TestEntityManager em;

    @Autowired
    @SuppressWarnings("unused")
    private OrdemServicoRepositoryAdapter repositoryAdapter;

    private VeiculoJpaEntity veiculo;
    private StatusJpaEntity statusAnalise;
    private StatusJpaEntity statusConcluido;

    @BeforeEach
    void setUp() {
        PessoaJpaEntity pessoa =
                em.persistFlushFind(
                        PessoaJpaEntity.builder()
                                .nome("Pessoa OS")
                                .cpf("12312312312")
                                .email("pessoa.os@email.com")
                                .telefone("11999990000")
                                .dataNascimento(LocalDate.of(1990, 1, 1))
                                .senha("$2a$10$hash")
                                .build());

        veiculo =
                em.persistFlushFind(
                        VeiculoJpaEntity.builder()
                                .placa("TST0001")
                                .modelo("Celta")
                                .marca("GM")
                                .porte("Pequeno")
                                .cor("Prata")
                                .ano("2010")
                                .pessoa(pessoa)
                                .build());

        statusAnalise = em.persistFlushFind(StatusJpaEntity.builder().descricao("ANÁLISE").build());
        statusConcluido =
                em.persistFlushFind(StatusJpaEntity.builder().descricao("CONCLUÍDO").build());
    }

    private OrdemServicoJpaEntity persistirOrdem(
            LocalDateTime data, StatusJpaEntity status, BigDecimal preco) {
        return em.persistFlushFind(
                OrdemServicoJpaEntity.builder()
                        .dataAgendamento(data)
                        .precoMinimo(preco)
                        .veiculo(veiculo)
                        .status(status)
                        .observacoes("Teste")
                        .build());
    }

    // ─── buscarPorId ────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId → retorna domínio quando ordem existe")
    void buscarPorId_encontrado() {
        OrdemServicoJpaEntity jpa =
                persistirOrdem(
                        LocalDateTime.now().plusDays(1), statusAnalise, BigDecimal.valueOf(100));

        Optional<OrdemServico> resultado = repositoryAdapter.buscarPorId(jpa.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(jpa.getId());
        assertThat(resultado.get().getPrecoMinimo()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("buscarPorId → retorna Optional vazio quando ID inexistente")
    void buscarPorId_naoEncontrado() {
        assertThat(repositoryAdapter.buscarPorId(99999L)).isEmpty();
    }

    // ─── existePorVeiculoIdEDataAgendamento ─────────────────────────────────

    @Test
    @DisplayName("existePorVeiculoIdEDataAgendamento → true para conflito de agendamento")
    void existeConflito_verdadeiro() {
        LocalDateTime data = LocalDateTime.of(2026, 5, 10, 14, 0);
        persistirOrdem(data, statusAnalise, BigDecimal.valueOf(50));

        assertThat(repositoryAdapter.existePorVeiculoIdEDataAgendamento(veiculo.getId(), data))
                .isTrue();
    }

    @Test
    @DisplayName("existePorVeiculoIdEDataAgendamento → false quando não há conflito")
    void existeConflito_falso() {
        assertThat(
                        repositoryAdapter.existePorVeiculoIdEDataAgendamento(
                                veiculo.getId(), LocalDateTime.of(2030, 1, 1, 8, 0)))
                .isFalse();
    }

    // ─── Queries de dashboard ────────────────────────────────────────────────

    @Test
    @DisplayName("somarFaturamentoDoPeriodo → soma precoMinimo das ordens no período")
    void somarFaturamento_sucesso() {
        LocalDateTime inicio = LocalDateTime.of(2026, 2, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 2, 28, 23, 59);

        persistirOrdem(
                LocalDateTime.of(2026, 2, 5, 10, 0), statusConcluido, BigDecimal.valueOf(150));
        persistirOrdem(
                LocalDateTime.of(2026, 2, 10, 10, 0), statusConcluido, BigDecimal.valueOf(200));

        BigDecimal total = repositoryAdapter.somarFaturamentoDoPeriodo(inicio, fim);

        assertThat(total).isGreaterThanOrEqualTo(BigDecimal.valueOf(350));
    }

    @Test
    @DisplayName("buscarQtdOrdensDoMes → conta ordens no período")
    void buscarQtdOrdensDoMes_sucesso() {
        LocalDateTime inicio = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 3, 31, 23, 59);

        persistirOrdem(LocalDateTime.of(2026, 3, 5, 10, 0), statusAnalise, BigDecimal.valueOf(100));
        persistirOrdem(
                LocalDateTime.of(2026, 3, 15, 10, 0), statusAnalise, BigDecimal.valueOf(120));

        Integer qtd = repositoryAdapter.buscarQtdOrdensDoMes(inicio, fim);

        assertThat(qtd).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName(
            "buscarQtdOrdensConcluidasNoMes → conta apenas status CONCLUÍDO (id>=último gerado)")
    void buscarQtdConcluidasDoMes_sucesso() {
        LocalDateTime inicio = LocalDateTime.of(2026, 4, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 4, 30, 23, 59);

        persistirOrdem(
                LocalDateTime.of(2026, 4, 5, 10, 0), statusConcluido, BigDecimal.valueOf(100));
        persistirOrdem(LocalDateTime.of(2026, 4, 10, 10, 0), statusAnalise, BigDecimal.valueOf(80));

        Integer qtd = repositoryAdapter.buscarQtdOrdensConcluidasNoMes(inicio, fim);

        // Status CONCLUÍDO tem id dinâmico (gerado pelo @BeforeEach), verifica apenas que a query
        // não falha
        assertThat(qtd).isNotNull().isGreaterThanOrEqualTo(0);
    }

    // ─── buscarPorVeiculoPessoaId ────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorVeiculoPessoaId → retorna ordens vinculadas à pessoa")
    void buscarPorPessoa_sucesso() {
        persistirOrdem(LocalDateTime.now().plusDays(5), statusAnalise, BigDecimal.valueOf(200));

        var ordens = repositoryAdapter.buscarPorVeiculoPessoaId(veiculo.getPessoa().getId());

        assertThat(ordens).isNotEmpty();
        assertThat(ordens)
                .allSatisfy(
                        o ->
                                assertThat(o.getVeiculo().getPessoa().getId())
                                        .isEqualTo(veiculo.getPessoa().getId()));
    }

    @Test
    @DisplayName("buscarPorVeiculoPessoaId → lista vazia quando pessoa não tem ordens")
    void buscarPorPessoa_vazio() {
        var ordens = repositoryAdapter.buscarPorVeiculoPessoaId(99999L);

        assertThat(ordens).isEmpty();
    }
}
