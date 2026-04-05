package com.automotiva.estetica.rick.infrastructure.repository.veiculo;

import com.automotiva.estetica.rick.infrastructure.entity.VeiculoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeiculoRepository extends JpaRepository<VeiculoEntity, Long> {

    List<VeiculoEntity> findByPessoa_Id(Long pessoaId);
}
