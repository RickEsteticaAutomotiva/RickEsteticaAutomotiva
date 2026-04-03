package com.automotiva.estetica.rick.adapter.out.persistence.itemservico;

import com.automotiva.estetica.rick.adapter.out.persistence.mapper.ItemServicoPersistenceMapper;
import com.automotiva.estetica.rick.application.port.out.ItemServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ItemServicoRepositoryAdapter implements ItemServicoRepositoryPort {

    private final ItemServicoJpaRepository jpaRepository;
    private final ItemServicoPersistenceMapper mapper;

    @Override
    public ItemServico salvar(ItemServico item) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(item)));
    }

    @Override
    public Optional<ItemServico> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ItemServico> buscarTodos() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ItemServico> buscarPorOrdemServicoId(Long ordemServicoId) {
        return jpaRepository.findByOrdemServico_Id(ordemServicoId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ItemServico> buscarPorOrdemServicoIdEServicoId(Long ordemServicoId, Long servicoId) {
        return jpaRepository.findByOrdemServico_IdAndServico_Id(ordemServicoId, servicoId).map(mapper::toDomain);
    }

    @Override
    public boolean existePorOrdemServicoIdEServicoId(Long ordemServicoId, Long servicoId) {
        return jpaRepository.existsByOrdemServico_IdAndServico_Id(ordemServicoId, servicoId);
    }

    @Override
    public void removerPorId(Long id) {
        jpaRepository.deleteById(id);
    }
}
