package com.automotiva.estetica.rick.api_agendamento_servicos.repository;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.Carinho;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarinhoRepository extends JpaRepository<Carinho, Long> {
    List<Carinho> findByUsuario(PessoaEntity usuario);

    Optional<Carinho> findByUsuarioAndServico(PessoaEntity usuario, ServicoEntity servico);

    boolean existsByUsuarioAndServico(PessoaEntity usuario, ServicoEntity servico);

    void deleteByUsuarioAndServico(PessoaEntity usuario, ServicoEntity servico);
}
