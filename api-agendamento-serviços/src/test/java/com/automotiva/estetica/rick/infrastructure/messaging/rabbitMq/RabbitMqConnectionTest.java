package com.automotiva.estetica.rick.infrastructure.messaging.rabbitMq;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RabbitMqConnectionTest {

    @Mock
    private AmqpAdmin amqpAdmin;

    @Test
    void addingQueues_deveDeclararFilaExchangeEBinding() {
        RabbitMqConnection connection = new RabbitMqConnection(amqpAdmin);

        ReflectionTestUtils.invokeMethod(connection, "addingQueues");

        verify(amqpAdmin).declareQueue(any(Queue.class));
        verify(amqpAdmin).declareExchange(any(DirectExchange.class));
        verify(amqpAdmin).declareBinding(any(Binding.class));
    }

    @Test
    void addingQueues_quandoFalha_deveRelancarRuntimeException() {
        RabbitMqConnection connection = new RabbitMqConnection(amqpAdmin);
        when(amqpAdmin.declareQueue(any(Queue.class))).thenThrow(new RuntimeException("rabbit indisponivel"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ReflectionTestUtils.invokeMethod(connection, "addingQueues"));

        assertTrue(ex.getMessage().contains("Falha ao inicializar RabbitMQ filas"));
    }
}
