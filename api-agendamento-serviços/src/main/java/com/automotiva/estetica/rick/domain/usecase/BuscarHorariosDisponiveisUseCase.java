package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.HorarioDisponivel;
import com.automotiva.estetica.rick.domain.entity.OrdemServicoDuracaoResumo;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.DataInvalidaException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class BuscarHorariosDisponiveisUseCase {

    private static final LocalTime INICIO_TRABALHO = LocalTime.of(9, 0);
    private static final LocalTime FIM_TRABALHO = LocalTime.of(17, 0);
    private final LocalDate dataLimiteValida = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
    private static final int MARGEM_ENTRE_SERVICOS = 10;

    private final ServicoGateway servicoGateway;
    private final OrdemServicoGateway ordemServicoGateway;

    public List<HorarioDisponivel> execute(LocalDate data, List<Long> servicosIds) {

        if (data.isBefore(dataLimiteValida)) {
            throw DataInvalidaException.builder().mensagem("A data informada é anterior ao dia atual").detalhes("")
                    .build();
        }

        LocalTime ponteiro = data.equals(dataLimiteValida)
                ? LocalTime.now(ZoneId.of("America/Sao_Paulo"))
                : INICIO_TRABALHO;
        if (ponteiro.isAfter(FIM_TRABALHO)) {
            return emptyList();
        }

        List<HorarioDisponivel> horariosDisponiveis = new ArrayList<>();
        List<Servico> servicos = servicoGateway.buscarPorIds(servicosIds);

        if (servicos.isEmpty()) {
            throw RecursoNaoEncontradoException.builder().mensagem("Servico nao encontrado").detalhes("").build();
        }

        int duracaoServicos = servicos.stream().mapToInt(Servico::getDuracaoMinutos).sum();

        List<OrdemServicoDuracaoResumo> ordensDoDia = ordemServicoGateway.buscarDuracaoTotalPorOS(data);

        for (OrdemServicoDuracaoResumo ordem : ordensDoDia) {
            int duracaoOS = Optional.ofNullable(ordem.duracaoTotal()).map(Long::intValue).orElse(0);
            LocalTime inicioOS = ordem.dataAgendamento().toLocalTime();
            LocalTime fimOS = inicioOS.plusMinutes(duracaoOS).plusMinutes(MARGEM_ENTRE_SERVICOS);

            ponteiro = definirHorariosDisponiveis(ponteiro, duracaoServicos, inicioOS, horariosDisponiveis);

            if (ponteiro.isBefore(fimOS)) {
                ponteiro = fimOS;
            }
        }

        definirHorariosDisponiveis(ponteiro, duracaoServicos, FIM_TRABALHO, horariosDisponiveis);

        return horariosDisponiveis;
    }

    private LocalTime definirHorariosDisponiveis(LocalTime horarioAtual, int duracaoNovaOrdem, LocalTime limiteHorario,
            List<HorarioDisponivel> horariosDisponiveis) {
        while (!horarioAtual.plusMinutes(duracaoNovaOrdem).isAfter(limiteHorario)) {
            LocalTime finalNovaOs = horarioAtual.plusMinutes(duracaoNovaOrdem);
            horariosDisponiveis.add(new HorarioDisponivel(horarioAtual, finalNovaOs));
            horarioAtual = finalNovaOs.plusMinutes(MARGEM_ENTRE_SERVICOS);
        }
        return horarioAtual;
    }
}
