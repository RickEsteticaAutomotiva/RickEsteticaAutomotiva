package com.automotiva.estetica.rick.api_agendamento_servicos.repository;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.FavoritoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<FavoritoEntity, Long> {
    @EntityGraph(attributePaths = {"servico"})
    List<FavoritoEntity> findByPessoaId(Long idPessoa);

    Optional<FavoritoEntity> findByPessoaAndServico(PessoaEntity pessoa, ServicoEntity servico);

    boolean existsByPessoaAndServico(PessoaEntity pessoa, ServicoEntity servico);

    void deleteByPessoaAndServico(PessoaEntity pessoa, ServicoEntity servico);
}
