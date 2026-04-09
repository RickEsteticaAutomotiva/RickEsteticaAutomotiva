package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizarValorItemServicoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ItemServicoGateway itemServicoGateway;

    public OrdemServico execute(Long ordemServicoId, Long servicoId, BigDecimal valorAplicado) {
        if (valorAplicado == null) {
            throw CampoInvalidoException.builder().mensagem("valorAplicado é obrigatório").detalhes("").build();
        }

        if (valorAplicado.compareTo(BigDecimal.ZERO) < 0) {
            throw CampoInvalidoException.builder().mensagem("valorAplicado deve ser maior ou igual a zero").detalhes("")
                    .build();
        }

        OrdemServico ordemServico = ordemServicoGateway.buscarPorIdComDetalhes(ordemServicoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("a ordem de serviço com id " + ordemServicoId + " não foi encontrada").detalhes("")
                        .build());

        ItemServico itemServico = itemServicoGateway.buscarPorOrdemServicoIdEServicoId(ordemServicoId, servicoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("o serviço " + servicoId + " não foi encontrado na ordem " + ordemServicoId)
                        .detalhes("").build());

        itemServico.setPreco(valorAplicado);
        itemServicoGateway.salvar(itemServico);

        return ordemServico;
    }
}
