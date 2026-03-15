package com.automotiva.estetica.rick.domain.entity;

import com.automotiva.estetica.rick.domain.enums.StatusOrdem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {

    private Long id;
    private LocalDateTime dataAgendamento;
    private BigDecimal precoMinimo;
    private Veiculo veiculo;
    private Status status;
    private String observacoes;
    private LocalDateTime dtConclusao;
    private MotivoCancelamento motivoCancelamento;

    public void atualizar(LocalDateTime dataAgendamento, BigDecimal precoMinimo, String observacoes, Long statusId,
            Long motivoId) {
        if (dataAgendamento != null)
            this.dataAgendamento = dataAgendamento;
        if (precoMinimo != null)
            this.precoMinimo = precoMinimo;
        if (observacoes != null)
            this.observacoes = observacoes;
        if (statusId != null) {
            this.status = Status.builder().id(statusId).build();
            // Regra de domínio: ao concluir a ordem, registra automaticamente a data/hora
            // de
            // conclusão
            if (StatusOrdem.CONCLUIDO.getId().equals(statusId)) {
                this.dtConclusao = LocalDateTime.now();
            }
        }
        if (motivoId != null)
            this.motivoCancelamento = MotivoCancelamento.builder().id(motivoId).build();
    }

    /**
     * Regra de domínio: cria um ItemServico vinculado a esta ordem, copiando o
     * preço atual do serviço no momento do agendamento.
     */
    public ItemServico criarItem(Servico servico) {
        if (servico == null)
            throw new IllegalArgumentException("Serviço não pode ser nulo ao criar item");
        return ItemServico.builder().servico(servico).ordemServico(this).preco(servico.getPreco()).build();
    }

    public boolean deveNotificarPorEmail() {
        return status != null && StatusOrdem.requerNotificacao(status.getId());
    }
}
