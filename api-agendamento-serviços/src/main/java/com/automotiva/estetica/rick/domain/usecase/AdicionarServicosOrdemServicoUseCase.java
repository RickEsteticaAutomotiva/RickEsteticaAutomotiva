package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdicionarServicosOrdemServicoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ItemServicoGateway itemServicoGateway;
    private final ServicoGateway servicoGateway;

    public OrdemServico execute(Long ordemServicoId, List<Long> servicoIds, List<BigDecimal> valoresAplicados) {
        if (servicoIds == null || servicoIds.isEmpty()) {
            throw CampoInvalidoException.builder().mensagem("é necessário informar ao menos um serviço").detalhes("")
                    .build();
        }

        OrdemServico ordemServico = ordemServicoGateway.buscarPorIdComDetalhes(ordemServicoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("a ordem de serviço com id " + ordemServicoId + " não foi encontrada").detalhes("")
                        .build());

        for (int i = 0; i < servicoIds.size(); i++) {
            Long servicoId = servicoIds.get(i);

            if (itemServicoGateway.existePorOrdemServicoIdEServicoId(ordemServicoId, servicoId)) {
                throw RecursoJaExisteException.builder()
                        .mensagem("o serviço " + servicoId + " já está vinculado a essa ordem").detalhes("").build();
            }

            Servico servico = servicoGateway.buscarPorId(servicoId).orElseThrow(() -> RecursoNaoEncontradoException
                    .builder().mensagem("serviço " + servicoId + " não encontrado").detalhes("").build());

            ItemServico itemServico = ordemServico.criarItem(servico);
            BigDecimal valorAplicado = valoresAplicados != null && i < valoresAplicados.size()
                    ? valoresAplicados.get(i)
                    : null;
            if (valorAplicado != null) {
                itemServico.setPreco(valorAplicado);
            }

            itemServicoGateway.salvar(itemServico);
        }

        return ordemServico;
    }
}
