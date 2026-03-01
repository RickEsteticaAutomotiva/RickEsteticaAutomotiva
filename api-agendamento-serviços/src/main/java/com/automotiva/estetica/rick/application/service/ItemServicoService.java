package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.dto.response.ItemServicoResponse;
import com.automotiva.estetica.rick.application.port.in.ItemServicoUseCase;
import com.automotiva.estetica.rick.application.port.out.ItemServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemServicoService implements ItemServicoUseCase {

    private final ItemServicoRepositoryPort itemServicoRepositoryPort;

    @Override
    public List<ItemServicoResponse> buscarTodos() {
        List<ItemServico> itens = itemServicoRepositoryPort.buscarTodos();
        if (itens.isEmpty()) {
            throw RecursoNaoEncontradoException.builder()
                    .mensagem("nenhum item de serviço foi encontrado")
                    .detalhes("")
                    .build();
        }
        return itens.stream().map(this::toResponse).toList();
    }

    @Override
    public ItemServicoResponse buscarPorId(Long id) {
        return itemServicoRepositoryPort
                .buscarPorId(id)
                .map(this::toResponse)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("o item com id " + id + " não foi encontrado")
                        .detalhes("")
                        .build());
    }

    @Override
    public List<ItemServicoResponse> listarPorOrdem(Long idOrdem) {
        return itemServicoRepositoryPort.buscarPorOrdemServicoId(idOrdem).stream()
                .map(this::toResponse)
                .toList();
    }


    private ItemServicoResponse toResponse(ItemServico i) {
        return ItemServicoResponse.builder()
                .id(i.getId())
                .idServico(i.getServico() != null ? i.getServico().getId() : null)
                .idOrdemServico(
                        i.getOrdemServico() != null ? i.getOrdemServico().getId() : null)
                .preco(i.getPreco())
                .build();
    }
}
