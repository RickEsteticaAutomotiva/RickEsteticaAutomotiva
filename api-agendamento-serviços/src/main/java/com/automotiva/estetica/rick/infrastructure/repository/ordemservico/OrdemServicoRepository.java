package com.automotiva.estetica.rick.infrastructure.repository.ordemservico;

import com.automotiva.estetica.rick.infrastructure.entity.OrdemServicoEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdemServicoRepository
        extends
            JpaRepository<OrdemServicoEntity, Long>,
            JpaSpecificationExecutor<OrdemServicoEntity> {

    boolean existsByVeiculoIdAndDataAgendamento(Long veiculoId, LocalDateTime dataAgendamento);

    @EntityGraph(attributePaths = {"veiculo", "veiculo.pessoa", "status", "motivoCancelamento"})
    Optional<OrdemServicoEntity> findOrdemServicoById(Long id);

    @EntityGraph(attributePaths = {"veiculo", "veiculo.pessoa", "status", "motivoCancelamento"})
    Page<OrdemServicoEntity> findAll(Specification<OrdemServicoEntity> spec, Pageable pageable);

    List<OrdemServicoEntity> findByVeiculo_Pessoa_Id(Long id);

    @Query(value = """
                SELECT
                    o.id AS id,
                    o.data_agendamento AS dataAgendamento,
                    COALESCE(SUM(s.duracao_minutos), 0) AS duracaoTotal
                FROM ordem_servico o
                JOIN item_servico i ON i.ordem_servico_id = o.id
                JOIN servico s ON s.id = i.servico_id
                WHERE CAST(o.data_agendamento AS DATE) = :data
                AND o.fk_status = 2
                GROUP BY o.id, o.data_agendamento
                ORDER BY o.data_agendamento ASC
            """, nativeQuery = true)
    List<OrdemServicoDuracaoProjection> buscarDuracaoTotalPorOS(@Param("data") LocalDate data);

    @Query("""
                SELECT COALESCE(SUM(o.precoMinimo), 0) FROM OrdemServicoEntity o
                WHERE o.dataAgendamento BETWEEN :inicio AND :fim
                  AND o.status.id <> 4
            """)
    BigDecimal somarFaturamentoDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT COUNT(o) FROM OrdemServicoEntity o WHERE o.dataAgendamento BETWEEN :inicio AND :fim")
    Integer buscarQtdOrdensDoMes(LocalDateTime inicio, LocalDateTime fim);

    @Query("""
                SELECT COUNT(o) FROM OrdemServicoEntity o
                WHERE o.dataAgendamento BETWEEN :inicio AND :fim AND o.status.id = 5
            """)
    Integer buscarQtdOrdensConcluidasNoMes(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(value = """
                SELECT COALESCE(SUM(ordem_total.total_ordem) / NULLIF(COUNT(ordem_total.ordem_id), 0), 0)
                FROM (
                    SELECT os.id AS ordem_id,
                           COALESCE(SUM(i.preco), 0) AS total_ordem
                    FROM ordem_servico os
                    LEFT JOIN item_servico i ON i.ordem_servico_id = os.id
                    WHERE os.data_agendamento BETWEEN :inicio AND :fim
                      AND os.fk_status = 5
                    GROUP BY os.id
                ) ordem_total
            """, nativeQuery = true)
    BigDecimal calcularTicketMedioDoMes(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("""
                SELECT CAST(i.ordemServico.dataAgendamento AS date) AS dia,
                       SUM(i.preco) AS totalDia
                FROM ItemServicoEntity i
                WHERE i.ordemServico.dataAgendamento >= :dataInicial
                  AND i.ordemServico.status.id = 5
                GROUP BY CAST(i.ordemServico.dataAgendamento AS date)
                ORDER BY CAST(i.ordemServico.dataAgendamento AS date)
            """)
    List<Object[]> buscarFaturamentoPorDia(@Param("dataInicial") LocalDateTime dataInicial);

    @Query("""
                SELECT i.servico.id,
                       i.servico.nome,
                       i.servico.categoria.id,
                       i.servico.categoria.nome,
                       COUNT(i.id),
                       COALESCE(SUM(i.preco), 0)
                FROM ItemServicoEntity i
                WHERE i.ordemServico.dataAgendamento BETWEEN :inicio AND :fim
                  AND i.ordemServico.status.id = 5
                GROUP BY i.servico.id, i.servico.nome, i.servico.categoria.id, i.servico.categoria.nome
                ORDER BY SUM(i.preco) DESC, i.servico.nome ASC
            """)
    List<Object[]> buscarFaturamentoServicos(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("""
                SELECT COALESCE(SUM(i.preco), 0)
                FROM ItemServicoEntity i
                WHERE i.ordemServico.dataAgendamento BETWEEN :inicio AND :fim
                  AND i.ordemServico.status.id = 5
            """)
    BigDecimal somarReceitaRecebidaDoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("""
                SELECT COALESCE(SUM(o.precoMinimo), 0)
                FROM OrdemServicoEntity o
                WHERE o.dataAgendamento BETWEEN :inicio AND :fim
                  AND o.status.id = 5
            """)
    BigDecimal somarCustoRealizadoDoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("""
                SELECT m.descricao,
                       COUNT(o.id)
                FROM OrdemServicoEntity o
                LEFT JOIN o.motivoCancelamento m
                WHERE o.dataAgendamento BETWEEN :inicio AND :fim
                  AND o.status.id = 4
                GROUP BY m.descricao
                ORDER BY COUNT(o.id) DESC, m.descricao ASC
            """)
    List<Object[]> buscarCancelamentosPorMotivoDoPeriodo(@Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query("""
                SELECT COUNT(o)
                FROM OrdemServicoEntity o
                WHERE o.dataAgendamento BETWEEN :inicio AND :fim
                  AND o.status.id <> :statusIdIgnorado
            """)
    long contarAgendamentosNoPeriodoExcetoStatus(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim,
            @Param("statusIdIgnorado") Long statusIdIgnorado);

    @Query("""
                SELECT COALESCE(SUM(o.precoMinimo), 0)
                FROM OrdemServicoEntity o
                WHERE o.dataAgendamento BETWEEN :inicio AND :fim
                  AND o.status.id <> :statusIdIgnorado
            """)
    BigDecimal somarFaturamentoEstimadoNoPeriodoExcetoStatus(@Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim, @Param("statusIdIgnorado") Long statusIdIgnorado);

    @EntityGraph(attributePaths = {"veiculo", "veiculo.pessoa", "status"})
    Optional<OrdemServicoEntity> findFirstByDataAgendamentoBetweenAndStatus_IdNotOrderByDataAgendamentoAscIdAsc(
            LocalDateTime inicio, LocalDateTime fim, Long statusIdIgnorado);

    @Query("""
                SELECT i.servico.nome
                FROM ItemServicoEntity i
                WHERE i.ordemServico.id = :ordemServicoId
                ORDER BY i.id ASC
            """)
    List<String> buscarNomesServicosDaOrdem(@Param("ordemServicoId") Long ordemServicoId);

    @EntityGraph(attributePaths = {"veiculo", "veiculo.pessoa", "status"})
    @Query("""
                SELECT o FROM OrdemServicoEntity o
                WHERE CAST(o.dataAgendamento AS date) = :data
                ORDER BY o.dataAgendamento ASC,
                         CASE WHEN o.status.descricao = 'CANCELADO' THEN 1 ELSE 0 END ASC
            """)
    List<OrdemServicoEntity> buscarAgendamentosDodia(@Param("data") LocalDate data);
}
