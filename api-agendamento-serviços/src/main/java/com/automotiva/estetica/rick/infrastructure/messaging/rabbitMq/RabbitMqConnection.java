package com.automotiva.estetica.rick.infrastructure.messaging.rabbitMq;

import com.automotiva.estetica.rick.constantes.RabbitMqConsts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("!test & !integration-test")
public class RabbitMqConnection {

    private final AmqpAdmin amqpAdmin;

    public RabbitMqConnection(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    private Queue ordemServicoCriadaQueue() {
        return new Queue(RabbitMqConsts.ORDEM_SERVICO_CRIADA_QUEUE, true, false, false);
    }

    private Queue ordemServicoAtualizadaQueue() {
        return new Queue(RabbitMqConsts.ORDEM_SERVICO_ATUALIZADA_QUEUE, true, false, false);
    }

    private DirectExchange directExchange() {
        return new DirectExchange(RabbitMqConsts.EXCHANGE_NAME);
    }

    private List<Binding> binding(List<Queue> queues, DirectExchange exchange) {
        return queues.stream().map(queue -> new Binding(queue.getName(), Binding.DestinationType.QUEUE,
                exchange.getName(), queue.getName(), null)).toList();
    }

    @PostConstruct
    private void addingQueues() {
        try {
            log.info("Inicializando filas do RabbitMQ...");
            List<Queue> queues = List.of(this.ordemServicoCriadaQueue(), this.ordemServicoAtualizadaQueue());
            DirectExchange exchange = this.directExchange();
            List<Binding> binding = this.binding(queues, exchange);

            queues.forEach(this.amqpAdmin::declareQueue);
            this.amqpAdmin.declareExchange(exchange);
            binding.forEach(this.amqpAdmin::declareBinding);

            log.info("Filas do RabbitMQ inicializadas com sucesso!");
        } catch (Exception e) {
            log.error("Erro ao inicializar filas do RabbitMQ", e);
            throw new RuntimeException("Falha ao inicializar RabbitMQ filas", e);
        }
    }
}
