package com.automotiva.estetica.rick.adapter.out.persistence.veiculo;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.VeiculoJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface VeiculoJpaRepository extends JpaRepository<VeiculoJpaEntity, Long> {

    List<VeiculoJpaEntity> findByPessoa_Id(Long pessoaId);
}
