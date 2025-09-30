package com.automotiva.estetica.rick.api_agendamento_servicos.repository;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServicoEntity, Long> {
//    Optional<OrdemServicoEntity> findByEmailAndSenha(Date email, String senha);
//    boolean existsByCpf(String cpf);
//    boolean existsByEmail(String email);
}
