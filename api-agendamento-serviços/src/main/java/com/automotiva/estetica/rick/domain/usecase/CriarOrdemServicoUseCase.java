package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.entity.Status;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoEventGateway;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriarOrdemServicoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ItemServicoGateway itemServicoGateway;
    private final ServicoGateway servicoGateway;
    private final OrdemServicoEventGateway ordemServicoEventGateway;

    public OrdemServico execute(LocalDateTime dataAgendamento, BigDecimal precoMinimo, Long veiculoId,
            String observacoes, List<Long> servicoIds) {
        if (ordemServicoGateway.existePorVeiculoIdEDataAgendamento(veiculoId, dataAgendamento)) {
            throw RecursoJaExisteException.builder().mensagem("um agendamento já existe nessa hora e data").detalhes("")
                    .build();
        }

        if (servicoIds == null || servicoIds.isEmpty()) {
            throw CampoInvalidoException.builder()
                    .mensagem("é necessário informar ao menos um serviço para criar a ordem").detalhes("").build();
        }

        OrdemServico ordemServico = OrdemServico.builder().dataAgendamento(dataAgendamento).precoMinimo(precoMinimo)
                .veiculo(Veiculo.builder().id(veiculoId).build()).status(Status.builder().id(1L).build())
                .observacoes(observacoes).build();

        ordemServico = ordemServicoGateway.salvar(ordemServico);

        List<String> nomesServicos = new ArrayList<>();
        for (Long servicoId : servicoIds) {
            Servico servico = servicoGateway.buscarPorId(servicoId).orElseThrow(() -> RecursoNaoEncontradoException
                    .builder().mensagem("serviço " + servicoId + " não encontrado").detalhes("").build());
            nomesServicos.add(servico.getNome());
            itemServicoGateway.salvar(ordemServico.criarItem(servico));
        }

        OrdemServico ordemComDetalhes = ordemServicoGateway.buscarPorIdComDetalhes(ordemServico.getId())
                .orElse(ordemServico);
        ordemServicoEventGateway.publicarOrdemServicoCriada(ordemComDetalhes, nomesServicos);

        return ordemComDetalhes;
    }
}
