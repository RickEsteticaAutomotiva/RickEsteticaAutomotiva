package com.automotiva.estetica.rick.api_agendamento_servicos.repository;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.VeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<VeiculoEntity, Long> {
    Optional<VeiculoEntity> findByPlaca(String placa);

    List<VeiculoEntity> findByPessoa_Id(Long idPessoa);
}
