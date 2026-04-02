package com.automotiva.estetica.rick.adapter.out.persistence.itemservico;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ItemServicoJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ItemServicoJpaRepository extends JpaRepository<ItemServicoJpaEntity, Long> {

    List<ItemServicoJpaEntity> findByOrdemServico_Id(Long ordemServicoId);

    Optional<ItemServicoJpaEntity> findByOrdemServico_IdAndServico_Id(Long ordemServicoId, Long servicoId);

    boolean existsByOrdemServico_IdAndServico_Id(Long ordemServicoId, Long servicoId);
}
