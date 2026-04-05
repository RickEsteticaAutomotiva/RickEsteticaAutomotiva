package com.automotiva.estetica.rick.domain.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.automotiva.estetica.rick.domain.entity.Email;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Status;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.gateway.EmailGateway;
import java.time.LocalDateTime;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificarAtualizacaoOrdemServicoUseCaseTest {

    @Mock
    private EmailGateway emailGateway;

    @InjectMocks
    private NotificarAtualizacaoOrdemServicoUseCase useCase;

    @Test
    void naoDeveEnviarEmailQuandoOrdemForNula() {
        useCase.execute(null);

        verify(emailGateway, never()).enviar(any());
    }

    @Test
    void deveEnviarEmailQuandoStatusExigirNotificacao() {
        OrdemServico ordemServico = OrdemServico.builder().id(10L).dataAgendamento(LocalDateTime.now().plusDays(1))
                .status(Status.builder().id(2L).descricao("EM_ANDAMENTO").build())
                .veiculo(Veiculo.builder().modelo("Civic")
                        .pessoa(Pessoa.builder().email("cliente@teste.com").nome("Cliente Teste").build()).build())
                .build();

        useCase.execute(ordemServico);

        ArgumentCaptor<Email> captor = ArgumentCaptor.forClass(Email.class);
        verify(emailGateway).enviar(captor.capture());

        Email emailCapturado = captor.getValue();
        assertEquals("cliente@teste.com", emailCapturado.getDestinatario());
        assertTrue(emailCapturado.getAssunto().contains("#10"));
        assertTrue(emailCapturado.getCorpo().contains("EM_ANDAMENTO"));
        assertTrue(emailCapturado.getCorpo().contains("Civic"));
    }

    @Test
    void naoDeveEnviarEmailQuandoStatusNaoExigirNotificacao() {
        OrdemServico ordemServico = OrdemServico.builder().id(11L).dataAgendamento(LocalDateTime.now().plusDays(1))
                .status(Status.builder().id(1L).descricao("AGUARDANDO").build())
                .veiculo(Veiculo.builder().modelo("Onix")
                        .pessoa(Pessoa.builder().email("cliente2@teste.com").nome("Cliente 2").build()).build())
                .build();

        useCase.execute(ordemServico);

        verify(emailGateway, never()).enviar(any());
    }

    @Test
    void naoDevePropagarErroAoEnviarEmail() {
        OrdemServico ordemServico = OrdemServico.builder().id(12L).dataAgendamento(LocalDateTime.now().plusDays(1))
                .status(Status.builder().id(5L).descricao("CONCLUIDO").build())
                .veiculo(Veiculo.builder().modelo("HB20")
                        .pessoa(Pessoa.builder().email("cliente3@teste.com").nome("Cliente 3").build()).build())
                .build();

        doThrow(new RuntimeException("falha smtp")).when(emailGateway).enviar(any());

        useCase.execute(ordemServico);

        verify(emailGateway).enviar(any());
    }
}
