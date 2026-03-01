package com.automotiva.estetica.rick.adapter.out.persistence.favorito;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.FavoritoJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.PessoaJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ServicoJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FavoritoJpaRepository extends JpaRepository<FavoritoJpaEntity, Long> {

    List<FavoritoJpaEntity> findByPessoaId(Long pessoaId);

    boolean existsByPessoaAndServico(PessoaJpaEntity pessoa, ServicoJpaEntity servico);
}
