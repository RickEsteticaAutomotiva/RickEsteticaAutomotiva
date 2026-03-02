package com.automotiva.estetica.rick.adapter.out.persistence.carrinho;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.CarrinhoJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.PessoaJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ServicoJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CarrinhoJpaRepository extends JpaRepository<CarrinhoJpaEntity, Long> {

    List<CarrinhoJpaEntity> findByPessoaId(Long pessoaId);

    boolean existsByPessoaAndServico(PessoaJpaEntity pessoa, ServicoJpaEntity servico);
}
