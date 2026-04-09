package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.infrastructure.repository.itemservico.ItemServicoRepository;
import com.automotiva.estetica.rick.infrastructure.mapper.ItemServicoEntityMapper;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemServicoGatewayImpl implements ItemServicoGateway {

    private final ItemServicoRepository itemServicoRepository;
    private final ItemServicoEntityMapper itemServicoEntityMapper;

    @Override
    public ItemServico salvar(ItemServico itemServico) {
        return itemServicoEntityMapper
                .toDomain(itemServicoRepository.save(itemServicoEntityMapper.toEntity(itemServico)));
    }

    @Override
    public Optional<ItemServico> buscarPorId(Long id) {
        return itemServicoRepository.findById(id).map(itemServicoEntityMapper::toDomain);
    }

    @Override
    public List<ItemServico> buscarTodos() {
        return itemServicoRepository.findAll().stream().map(itemServicoEntityMapper::toDomain).toList();
    }

    @Override
    public List<ItemServico> buscarPorOrdemServicoId(Long ordemServicoId) {
        return itemServicoRepository.findByOrdemServico_Id(ordemServicoId).stream()
                .map(itemServicoEntityMapper::toDomain).toList();
    }

    @Override
    public Optional<ItemServico> buscarPorOrdemServicoIdEServicoId(Long ordemServicoId, Long servicoId) {
        return itemServicoRepository.findByOrdemServico_IdAndServico_Id(ordemServicoId, servicoId)
                .map(itemServicoEntityMapper::toDomain);
    }

    @Override
    public boolean existePorOrdemServicoIdEServicoId(Long ordemServicoId, Long servicoId) {
        return itemServicoRepository.existsByOrdemServico_IdAndServico_Id(ordemServicoId, servicoId);
    }

    @Override
    public void removerPorId(Long id) {
        itemServicoRepository.deleteById(id);
    }
}
