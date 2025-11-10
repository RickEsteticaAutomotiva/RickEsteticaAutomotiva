package com.automotiva.estetica.rick.api_agendamento_servicos.repository;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ItemServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemServicoRepository extends JpaRepository<ItemServicoEntity, Long> {
    List<ItemServicoEntity> findByOrdemServicoId(Long idOrdem);
}
