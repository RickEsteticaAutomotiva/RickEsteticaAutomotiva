package com.automotiva.estetica.rick.infrastructure.messaging.rabbitMq;

import com.automotiva.estetica.rick.constantes.RabbitMqConsts;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqConnection {

    private AmqpAdmin amqpAdmin;

    public RabbitMqConnection(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    private Queue queue(String nomeQueue) {
        return new Queue(nomeQueue, true, false, false);
    }

    private DirectExchange directExchange() {
        return new DirectExchange(RabbitMqConsts.EXCHANGE_NAME);
    }

    private Binding binding(Queue queue, DirectExchange exchange) {
        return new Binding(
                queue.getName(),
                Binding.DestinationType.QUEUE,
                exchange.getName(),
                queue.getName(),
                null);
    }

    @PostConstruct
    private void addingQueues() {
        Queue ordemServicoCriadaQueue = this.queue(RabbitMqConsts.ORDEM_SERVICO_CRIADA_QUEUE);
        DirectExchange exchange = this.directExchange();
        Binding binding = this.binding(ordemServicoCriadaQueue, exchange);

        this.amqpAdmin.declareQueue(ordemServicoCriadaQueue);
        this.amqpAdmin.declareExchange(exchange);
        this.amqpAdmin.declareBinding(binding);
    }
}
