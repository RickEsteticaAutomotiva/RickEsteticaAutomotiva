package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListarItensServicoUseCase {

    private final ItemServicoGateway itemServicoGateway;

    public List<ItemServico> execute() {
        List<ItemServico> itens = itemServicoGateway.buscarTodos();
        if (itens.isEmpty()) {
            throw RecursoNaoEncontradoException.builder().mensagem("nenhum item de serviço foi encontrado").detalhes("")
                    .build();
        }
        return itens;
    }
}
