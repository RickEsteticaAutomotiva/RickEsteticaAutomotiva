package com.automotiva.estetica.rick.api_agendamento_servicos.repository;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.FaturamentoMensalDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.RegistroFaturamentoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
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
public interface OrdemServicoRepository
        extends JpaRepository<OrdemServicoEntity, Long>, JpaSpecificationExecutor<OrdemServicoEntity> {
    boolean existsByVeiculoIdAndDataAgendamento(Long veiculoId, LocalDateTime dataAgendamento);

    @EntityGraph(attributePaths = {"veiculo", "veiculo.pessoa", "status"})
    Optional<OrdemServicoEntity> findOrdemServicoEntityById(Long id);

    List<OrdemServicoEntity> findByVeiculo_Pessoa_Id(Long id);

    @Query(
            """
                SELECT COALESCE(SUM(o.precoMinimo), 0) FROM OrdemServicoEntity o
                WHERE o.dataAgendamento BETWEEN :inicio AND :fim AND o.status.id = 5
            """)
    BigDecimal somarFaturamentoDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT COUNT(o) FROM OrdemServicoEntity o WHERE o.dataAgendamento BETWEEN :inicio AND :fim")
    Integer buscarQtdOrdensServicoDoMes(LocalDateTime inicio, LocalDateTime fim);

    @Query(
            """
                        SELECT COUNT(o) FROM OrdemServicoEntity o
                        WHERE o.dataAgendamento BETWEEN :inicio AND :fim AND o.status.id = 5
                    """)
    Integer buscarQtdOrdensServicoConcluidasNoMes(
            @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("""
    SELECT new com.automotiva.estetica.rick.api_agendamento_servicos.dto.FaturamentoMensalDto(
        (SELECT COALESCE(SUM(o2.precoMinimo), 0)
         FROM OrdemServicoEntity o2
         WHERE o2.status.id = 5
           AND o2.dataAgendamento BETWEEN :inicio AND :fim),
        COALESCE(SUM(i.preco * s.margemLucro), 0)
    )
    FROM ItemServicoEntity i
    JOIN i.servico s
    JOIN i.ordemServico o
    WHERE o.status.id = 5
      AND o.dataAgendamento BETWEEN :inicio AND :fim
    """)
    FaturamentoMensalDto buscarFaturamentoPorMes(@Param("inicio") LocalDateTime inicio,
                                                 @Param("fim") LocalDateTime fim);


    @Query(value = """
    SELECT
    m.descricao AS tipo,
    COUNT(os.id) AS quantidade
    FROM ordem_servico os
    LEFT JOIN motivo m ON m.id = os.fk_motivo
    WHERE os.fk_status = 4
      AND os.data_agendamento >= DATEADD('DAY', -30, CURRENT_DATE)
    GROUP BY m.descricao
    ORDER BY quantidade DESC;
    """,
            nativeQuery = true)
    List<Object[]> buscarCancelamentos();

    @Query(
            """
                        SELECT COALESCE(SUM(i.preco) / COUNT(i.preco), 0)
                        FROM ItemServicoEntity i
                        WHERE i.ordemServico.dataAgendamento BETWEEN :inicio AND :fim
                          AND i.ordemServico.status.id = 5
                    """)
    BigDecimal calcularTicketMedioDoMes(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(
            """
                SELECT
                CAST(i.ordemServico.dataAgendamento AS date) AS dia,
                SUM(i.preco) AS totalDia
                FROM ItemServicoEntity i
                WHERE i.ordemServico.dataAgendamento >= :dataInicial
                AND i.ordemServico.status.id = 5
                GROUP BY CAST(i.ordemServico.dataAgendamento AS date)
                ORDER BY dia DESC
            """)
    List<Object[]> buscarOrdensConcluidasPeriodo(@Param("dataInicial") LocalDateTime dataInicial);
}
