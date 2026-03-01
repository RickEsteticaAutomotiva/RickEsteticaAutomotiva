package com.automotiva.estetica.rick.adapter.out.persistence.ordemservico;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.OrdemServicoJpaEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface OrdemServicoJpaRepository
        extends JpaRepository<OrdemServicoJpaEntity, Long>, JpaSpecificationExecutor<OrdemServicoJpaEntity> {

    boolean existsByVeiculoIdAndDataAgendamento(Long veiculoId, LocalDateTime dataAgendamento);

    @EntityGraph(attributePaths = {"veiculo", "veiculo.pessoa", "status"})
    Optional<OrdemServicoJpaEntity> findOrdemServicoById(Long id);

    List<OrdemServicoJpaEntity> findByVeiculo_Pessoa_Id(Long id);

    @Query(
            """
        SELECT COALESCE(SUM(o.precoMinimo), 0) FROM OrdemServicoJpaEntity o
        WHERE o.dataAgendamento BETWEEN :inicio AND :fim
    """)
    BigDecimal somarFaturamentoDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT COUNT(o) FROM OrdemServicoJpaEntity o WHERE o.dataAgendamento BETWEEN :inicio AND :fim")
    Integer buscarQtdOrdensDoMes(LocalDateTime inicio, LocalDateTime fim);

    @Query(
            """
        SELECT COUNT(o) FROM OrdemServicoJpaEntity o
        WHERE o.dataAgendamento BETWEEN :inicio AND :fim AND o.status.id = 5
    """)
    Integer buscarQtdOrdensConcluidasNoMes(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(
            """
        SELECT COALESCE(SUM(i.preco) / COUNT(i.preco), 0)
        FROM ItemServicoJpaEntity i
        WHERE i.ordemServico.dataAgendamento BETWEEN :inicio AND :fim
          AND i.ordemServico.status.id = 5
    """)
    BigDecimal calcularTicketMedioDoMes(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(
            """
        SELECT CAST(i.ordemServico.dataAgendamento AS date) AS dia,
               SUM(i.preco) AS totalDia
        FROM ItemServicoJpaEntity i
        WHERE i.ordemServico.dataAgendamento >= :dataInicial
          AND i.ordemServico.status.id = 5
        GROUP BY CAST(i.ordemServico.dataAgendamento AS date)
        ORDER BY dia
    """)
    List<Object[]> buscarFaturamentoPorDia(LocalDateTime dataInicial);
}
