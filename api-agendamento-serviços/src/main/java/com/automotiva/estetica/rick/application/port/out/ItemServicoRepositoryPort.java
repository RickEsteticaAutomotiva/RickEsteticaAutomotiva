package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.domain.entity.ItemServico;
import java.util.List;
import java.util.Optional;

public interface ItemServicoRepositoryPort {

    ItemServico salvar(ItemServico itemServico);

    Optional<ItemServico> buscarPorId(Long id);

    List<ItemServico> buscarTodos();

    List<ItemServico> buscarPorOrdemServicoId(Long ordemServicoId);

    Optional<ItemServico> buscarPorOrdemServicoIdEServicoId(Long ordemServicoId, Long servicoId);

    boolean existePorOrdemServicoIdEServicoId(Long ordemServicoId, Long servicoId);

    void removerPorId(Long id);
}
