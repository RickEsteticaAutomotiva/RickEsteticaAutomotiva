package com.automotiva.estetica.rick.api_agendamento_servicos.repository;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<PessoaEntity, Long>, JpaSpecificationExecutor<PessoaEntity> {
    Optional<PessoaEntity> findByEmailAndSenha(String email, String senha);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
