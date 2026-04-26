package com.automotiva.estetica.rick.infrastructure.messaging;

import com.automotiva.estetica.rick.constantes.RabbitMqConsts;
import com.automotiva.estetica.rick.domain.entity.MotivoCancelamento;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoEventGateway;
import com.automotiva.estetica.rick.dto.OrdemServicoAtualizadaEvent;
import com.automotiva.estetica.rick.dto.OrdemServicoCriadaEvent;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitOrdemServicoPublisher implements OrdemServicoEventGateway {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publicarOrdemServicoCriada(OrdemServico ordemServico, List<String> nomesServicos) {

        String observacoes = (ordemServico.getObservacoes() != null && !ordemServico.getObservacoes().isBlank())
                ? ordemServico.getObservacoes()
                : null;

        OrdemServicoCriadaEvent event = new OrdemServicoCriadaEvent(ordemServico.getId(),
                ordemServico.getVeiculo().getPlaca(), ordemServico.getDataAgendamento(), nomesServicos, observacoes);

        log.info("Enviando evento para a fila de criação de ordem de serviço | id ordem de serviço: {}",
                event.IdOrdemServico());

        rabbitTemplate.convertAndSend(RabbitMqConsts.ORDEM_SERVICO_CRIADA_QUEUE, event);
    }

    @Override
    public void publicarOrdemServicoAtualizada(OrdemServico ordemServico) {
        OrdemServicoAtualizadaEvent event = new OrdemServicoAtualizadaEvent(ordemServico.getId(),
                ordemServico.getDataAgendamento(), ordemServico.getPrecoMinimo(),
                ordemServico.getObservacoes() != null ? ordemServico.getObservacoes() : null,
                ordemServico.getStatus().getId(),
                Optional.ofNullable(ordemServico.getMotivoCancelamento()).map(MotivoCancelamento::getId).orElse(null));

        log.info("Enviando evento para a fila de atualização de ordem de serviço | id ordem de serviço: {}",
                ordemServico.getId());
        rabbitTemplate.convertAndSend(RabbitMqConsts.ORDEM_SERVICO_ATUALIZADA_QUEUE, event);
    }
}
