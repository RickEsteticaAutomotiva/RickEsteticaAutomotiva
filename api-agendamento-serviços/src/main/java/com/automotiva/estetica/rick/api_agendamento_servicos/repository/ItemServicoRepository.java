package com.automotiva.estetica.rick.api_agendamento_servicos.repository;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.RegistroFaturamentoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ItemServicoEntity;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemServicoRepository extends JpaRepository<ItemServicoEntity, Long> {
    List<ItemServicoEntity> findByOrdemServicoId(Long idOrdem);

    @Query("""
    SELECT new com.automotiva.estetica.rick.api_agendamento_servicos.dto.RegistroFaturamentoDto(
        c.nome,
        s.nome,
        SUM(i.preco)
    )
        FROM ItemServicoEntity i
        JOIN i.servico s
        JOIN s.categoria c
        JOIN i.ordemServico os
        WHERE os.dataAgendamento BETWEEN :inicio AND :fim
          AND os.status.id = 5
        GROUP BY c.nome, s.nome
        ORDER BY c.nome, s.nome
    """)
    List<RegistroFaturamentoDto> buscarFaturamentoAgrupado(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

}
