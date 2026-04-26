package com.automotiva.estetica.rick.infrastructure.messaging.rabbitMq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de configuração do RabbitMQ")
class RabbitMqConnectionTest {

    @Mock
    private AmqpAdmin amqpAdmin;

    @InjectMocks
    private RabbitMqConnection rabbitMqConnection;

    @BeforeEach
    void setup() {
    }

    @Test
    @DisplayName("Deve criar filas, exchange e bindings com sucesso")
    void adicionarFilas_deveCriarFilasExchangeEBindingsComSucesso() {

        ReflectionTestUtils.invokeMethod(rabbitMqConnection, "addingQueues");

        verify(amqpAdmin, times(2)).declareQueue(any());
        verify(amqpAdmin, times(1)).declareExchange(any());
        verify(amqpAdmin, times(2)).declareBinding(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando falhar criação das filas")
    void adicionarFilas_deveLancarExcecaoQuandoFalharCriacaoFilas() {

        doThrow(new RuntimeException("Erro")).when(amqpAdmin).declareQueue(any());

        assertThrows(RuntimeException.class,
                () -> ReflectionTestUtils.invokeMethod(rabbitMqConnection, "addingQueues"));

        verify(amqpAdmin, atLeastOnce()).declareQueue(any());
    }
}
