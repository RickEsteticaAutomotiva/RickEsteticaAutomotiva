package com.automotiva.estetica.rick.adapter.out.persistence;

import com.automotiva.estetica.rick.adapter.out.persistence.jpa.FavoritoJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpa.PessoaJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpa.ServicoJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FavoritoJpaRepository extends JpaRepository<FavoritoJpaEntity, Long> {

    List<FavoritoJpaEntity> findByPessoaId(Long pessoaId);

    boolean existsByPessoaAndServico(PessoaJpaEntity pessoa, ServicoJpaEntity servico);
}
