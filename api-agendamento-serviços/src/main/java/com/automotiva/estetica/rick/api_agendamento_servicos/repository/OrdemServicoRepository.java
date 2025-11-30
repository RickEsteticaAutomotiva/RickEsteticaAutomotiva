package com.automotiva.estetica.rick.api_agendamento_servicos.repository;

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
                WHERE o.dataAgendamento BETWEEN :inicio AND :fim
            """)
    BigDecimal somarFaturamentoDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT COUNT(o) FROM OrdemServicoEntity o WHERE o.dataAgendamento BETWEEN :inicio AND :fim")
    Integer findQtdOrdensServicoDoMes(LocalDateTime inicio, LocalDateTime fim);

    @Query(
            """
                SELECT COUNT(o) FROM OrdemServicoEntity o
                WHERE o.dataAgendamento BETWEEN :inicio AND :fim AND o.status.id = 5
            """)
    Integer findQtdOrdensServicoConcluidasNoMes(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query(
            """
                SELECT COALESCE(SUM(i.preco) / COUNT(i.preco), 0)
                FROM ItemServicoEntity i
                WHERE i.ordemServico.dataAgendamento BETWEEN :inicio AND :fim
                  AND i.ordemServico.status.id = 5
            """)
    BigDecimal calcularTicketMedioDoMes(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
