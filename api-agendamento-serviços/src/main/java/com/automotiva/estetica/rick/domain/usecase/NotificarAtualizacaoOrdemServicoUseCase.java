package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Email;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.gateway.EmailGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificarAtualizacaoOrdemServicoUseCase {

    private final EmailGateway emailGateway;

    public void execute(OrdemServico ordemServico) {
        if (ordemServico == null || !ordemServico.deveNotificarPorEmail()) {
            return;
        }

        try {
            Email email = new Email();
            email.setAssunto("Atualização de Status da Ordem de Serviço #" + ordemServico.getId());
            email.setCorpo(String.format(
                    "A ordem de serviço #%d teve seu status atualizado para: %s%nCliente: %s%nVeículo: %s%nData: %s",
                    ordemServico.getId(), ordemServico.getStatus().getDescricao(),
                    ordemServico.getVeiculo().getPessoa().getNome(), ordemServico.getVeiculo().getModelo(),
                    ordemServico.getDataAgendamento()));
            email.setDestinatario(ordemServico.getVeiculo().getPessoa().getEmail());
            emailGateway.enviar(email);
        } catch (Exception e) {
            log.warn("Falha ao enviar e-mail de atualização para ordem {}: {}", ordemServico.getId(), e.getMessage());
        }
    }
}
