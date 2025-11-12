package com.automotiva.estetica.rick.api_agendamento_servicos.repository;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServicoEntity, Long>, JpaSpecificationExecutor<OrdemServicoEntity> {
    boolean existsByVeiculoIdAndDataAgendamento(Long veiculoId, LocalDateTime dataAgendamento);
    @EntityGraph(attributePaths = {"veiculo", "veiculo.pessoa", "status"})
    Optional<OrdemServicoEntity> findOrdemServicoEntityById(Long id);
    List<OrdemServicoEntity> findByVeiculo_Pessoa_Id(Long id);
}
