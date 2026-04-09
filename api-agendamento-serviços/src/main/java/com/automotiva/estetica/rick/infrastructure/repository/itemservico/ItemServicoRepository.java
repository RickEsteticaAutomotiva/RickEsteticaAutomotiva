package com.automotiva.estetica.rick.infrastructure.repository.itemservico;

import com.automotiva.estetica.rick.infrastructure.entity.ItemServicoEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemServicoRepository extends JpaRepository<ItemServicoEntity, Long> {

    List<ItemServicoEntity> findByOrdemServico_Id(Long ordemServicoId);

    Optional<ItemServicoEntity> findByOrdemServico_IdAndServico_Id(Long ordemServicoId, Long servicoId);

    boolean existsByOrdemServico_IdAndServico_Id(Long ordemServicoId, Long servicoId);
}
