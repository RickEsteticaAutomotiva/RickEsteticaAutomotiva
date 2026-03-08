package com.automotiva.estetica.rick.adapter.out.messaging;

import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import com.automotiva.estetica.rick.application.port.out.OrdemServicoEventPublisherPort;
import com.automotiva.estetica.rick.application.port.out.ServicoRepositoryPort;
import com.automotiva.estetica.rick.constantes.RabbitMqConsts;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.dto.OrdemServicoCriadaEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitOrdemServicoPublisher implements OrdemServicoEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;
    private final ServicoRepositoryPort servicoRepositoryPort;

    @Override
    public void publicarOrdemServicoCriada(
            OrdemServico ordemServico, OrdemServicoRequest ordemServicoRequest) {

        List<String> servicos =
                servicoRepositoryPort.buscarPorIds(ordemServicoRequest.getServicos())
                        .stream()
                        .map(Servico::getNome)
                        .toList();

        String observacoes =
                (ordemServico.getObservacoes() != null && !ordemServico.getObservacoes().isBlank())
                        ? ordemServico.getObservacoes()
                        : null;

        OrdemServicoCriadaEvent event =
                new OrdemServicoCriadaEvent(
                        ordemServico.getId(),
                        ordemServico.getVeiculo().getPlaca(),
                        ordemServico.getDataAgendamento(),
                        servicos,
                        observacoes);

        log.info("Enviando evento para a fila de criação de ordem de serviço: {}", event);

        rabbitTemplate.convertAndSend(RabbitMqConsts.ORDEM_SERVICO_CRIADA_QUEUE, event);
    }
}