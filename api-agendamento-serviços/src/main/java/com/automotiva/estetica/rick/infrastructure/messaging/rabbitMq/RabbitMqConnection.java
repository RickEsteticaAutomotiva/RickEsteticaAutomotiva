package com.automotiva.estetica.rick.infrastructure.messaging.rabbitMq;

import com.automotiva.estetica.rick.constantes.RabbitMqConsts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMqConnection {

    private final AmqpAdmin amqpAdmin;

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
        return new Binding(queue.getName(), Binding.DestinationType.QUEUE, exchange.getName(), queue.getName(), null);
    }

    @PostConstruct
    private void addingQueues() {
        try {
            log.info("Inicializando filas do RabbitMQ...");
            Queue ordemServicoCriadaQueue = this.queue(RabbitMqConsts.ORDEM_SERVICO_CRIADA_QUEUE);
            DirectExchange exchange = this.directExchange();
            Binding binding = this.binding(ordemServicoCriadaQueue, exchange);

            this.amqpAdmin.declareQueue(ordemServicoCriadaQueue);
            this.amqpAdmin.declareExchange(exchange);
            this.amqpAdmin.declareBinding(binding);
            
            log.info("✅ Filas do RabbitMQ inicializadas com sucesso!");
        } catch (Exception e) {
            log.error("❌ Erro ao inicializar filas do RabbitMQ", e);
            throw new RuntimeException("Falha ao inicializar RabbitMQ filas", e);
        }
    }
}


