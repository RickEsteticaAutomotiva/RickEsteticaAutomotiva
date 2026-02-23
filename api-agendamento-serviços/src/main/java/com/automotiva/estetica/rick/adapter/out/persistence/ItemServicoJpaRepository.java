package com.automotiva.estetica.rick.adapter.out.persistence;

import com.automotiva.estetica.rick.adapter.out.persistence.jpa.ItemServicoJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ItemServicoJpaRepository extends JpaRepository<ItemServicoJpaEntity, Long> {

    List<ItemServicoJpaEntity> findByOrdemServicoId(Long ordemServicoId);
}
