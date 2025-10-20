package com.automotiva.estetica.rick.api_agendamento_servicos.service.observer;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.EmailEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailObserver implements OrdemServicoObserver {
    private final EmailService emailService;

    @Override
    public void update(OrdemServicoEntity ordemServico) {
        EmailEntity email = new EmailEntity();
        email.setAssunto("Atualização de Status da Ordem de Serviço #" + ordemServico.getId());
        email.setCorpo(String.format(
                "A ordem de serviço #%d teve seu status atualizado para: %s\n" +
                        "Cliente: %s\n" +
                        "Veículo: %s\n" +
                        "Data do Serviço: %s",
                ordemServico.getId(),
                ordemServico.getStatus().getDescricao(),
                ordemServico.getVeiculo().getPessoa().getNome(),
                ordemServico.getVeiculo().getModelo(),
                ordemServico.getDataAgendamento()
        ));
        email.setDestinatario(ordemServico.getVeiculo().getPessoa().getEmail());

        emailService.enviarEmailComAnexos(email,null);
    }
}