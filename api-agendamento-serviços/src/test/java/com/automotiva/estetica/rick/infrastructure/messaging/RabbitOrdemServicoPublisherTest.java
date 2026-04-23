package com.automotiva.estetica.rick.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.automotiva.estetica.rick.constantes.RabbitMqConsts;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Status;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.dto.OrdemServicoAtualizadaEvent;
import com.automotiva.estetica.rick.dto.OrdemServicoCriadaEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de RabbitOrdemServicoPublisher")
class RabbitOrdemServicoPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitOrdemServicoPublisher rabbitOrdemServicoPublisher;

    @Test
    @DisplayName("deve publicar evento com observacoes quando valor nao for blank")
    void publicarOrdemServicoCriada_devePublicarEventoComObservacoes() {
        LocalDateTime dataAgendamento = LocalDateTime.of(2026, 4, 3, 20, 0);
        OrdemServico ordemServico = OrdemServico.builder().id(55L).dataAgendamento(dataAgendamento)
                .veiculo(Veiculo.builder().placa("ABC1D23").build()).observacoes("Cliente pediu polimento").build();
        List<String> nomesServicos = List.of("Polimento");

        rabbitOrdemServicoPublisher.publicarOrdemServicoCriada(ordemServico, nomesServicos);

        ArgumentCaptor<OrdemServicoCriadaEvent> eventCaptor = ArgumentCaptor.forClass(OrdemServicoCriadaEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMqConsts.ORDEM_SERVICO_CRIADA_QUEUE), eventCaptor.capture());

        OrdemServicoCriadaEvent expected = new OrdemServicoCriadaEvent(55L, "ABC1D23", dataAgendamento, nomesServicos,
                "Cliente pediu polimento");
        assertEquals(expected, eventCaptor.getValue());
    }

    @Test
    @DisplayName("deve publicar evento com observacoes nulas quando texto for blank")
    void publicarOrdemServicoCriada_deveNormalizarObservacoesBlankParaNull() {
        LocalDateTime dataAgendamento = LocalDateTime.of(2026, 4, 3, 21, 0);
        OrdemServico ordemServico = OrdemServico.builder().id(99L).dataAgendamento(dataAgendamento)
                .veiculo(Veiculo.builder().placa("XYZ9W88").build()).observacoes("   ").build();
        List<String> nomesServicos = List.of("Lavagem", "Higienizacao");

        rabbitOrdemServicoPublisher.publicarOrdemServicoCriada(ordemServico, nomesServicos);

        ArgumentCaptor<OrdemServicoCriadaEvent> eventCaptor = ArgumentCaptor.forClass(OrdemServicoCriadaEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMqConsts.ORDEM_SERVICO_CRIADA_QUEUE), eventCaptor.capture());

        OrdemServicoCriadaEvent expected = new OrdemServicoCriadaEvent(99L, "XYZ9W88", dataAgendamento, nomesServicos,
                null);
        assertEquals(expected, eventCaptor.getValue());
    }

    @DisplayName("deve publicar evento de atualizacao com observacoes preenchidas")
    void publicarOrdemServicoAtualizada_devePublicarEventoComObservacoes() {
        LocalDateTime dataAgendamento = LocalDateTime.of(2026, 4, 3, 20, 0);

        OrdemServico ordemServico = OrdemServico.builder().id(55L).dataAgendamento(dataAgendamento)
                .precoMinimo(BigDecimal.valueOf(150)).observacoes("Atualizado com polimento")
                .veiculo(Veiculo.builder().placa("ABC1D23").build()).status(Status.builder().id(2L).build()).build();

        rabbitOrdemServicoPublisher.publicarOrdemServicoAtualizada(ordemServico);

        ArgumentCaptor<OrdemServicoAtualizadaEvent> eventCaptor = ArgumentCaptor
                .forClass(OrdemServicoAtualizadaEvent.class);

        verify(rabbitTemplate).convertAndSend(eq(RabbitMqConsts.ORDEM_SERVICO_ATUALIZADA_QUEUE), eventCaptor.capture());

        OrdemServicoAtualizadaEvent expected = new OrdemServicoAtualizadaEvent(55L, dataAgendamento,
                BigDecimal.valueOf(150), "Atualizado com polimento", 2L, null);

        assertEquals(expected, eventCaptor.getValue());
    }
}
