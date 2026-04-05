package com.automotiva.estetica.rick.infrastructure.repository.favorito;

import com.automotiva.estetica.rick.infrastructure.entity.FavoritoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.PessoaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.ServicoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritoRepository extends JpaRepository<FavoritoEntity, Long> {

    List<FavoritoEntity> findByPessoaId(Long pessoaId);

    boolean existsByPessoaIdAndServicoId(Long pessoaId, Long servicoId);

    boolean existsByPessoaAndServico(PessoaEntity pessoa, ServicoEntity servico);
}
