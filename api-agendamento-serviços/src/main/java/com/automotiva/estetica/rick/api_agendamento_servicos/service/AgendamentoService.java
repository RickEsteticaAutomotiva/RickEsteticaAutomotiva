package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.AgendamentoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.AgendamentoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComListaObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoSemObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.AgendamentoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class AgendamentoService {
    @Autowired
    EventoGoogleCalendarService eventoGoogleCalendarService;
    @Autowired
    AgendamentoRepository agendamentoRepository;
    @Autowired
    VeiculoRepository veiculoRepository;

    private AgendamentoEntity agendamentoEntity;

    public RetornoSemObjeto cadastrarAgendamento(AgendamentoDto agendamentoDto) {
        var retorno = new RetornoSemObjeto();

        try {
            var veiculo = veiculoRepository.findByPlaca(agendamentoDto.getPlaca())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

            var inicio = Date.from(agendamentoDto.getDataHora().atZone(ZoneId.systemDefault()).toInstant());
            var fim = Date.from(agendamentoDto.getDataHora().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());

            String eventId = eventoGoogleCalendarService.criarEvento(
                    "Agendamento - " + veiculo.getModelo(),
                    "Serviço para o veículo: " + veiculo.getPlaca(),
                    inicio,
                    fim
            );

            var novoAgendamento = AgendamentoEntity.builder()
                    .dataHora(agendamentoDto.getDataHora())
                    .status(agendamentoDto.getStatus())
                    .veiculo(veiculo)
                    .googleEventId(eventId)
                    .build();

            agendamentoRepository.save(novoAgendamento);

            retorno.setStatusCode(201);
            retorno.setMensagem("Agendamento realizado com sucesso!");
        } catch (Exception e) {
            retorno.setStatusCode(500);
            retorno.setMensagem("Erro ao realizar agendamento: " + e.getMessage());
        }

        return retorno;
    }

    public RetornoComListaObjeto<AgendamentoEntity> buscarAgendamentos() {

        try {
            List<AgendamentoEntity> agendamentos = agendamentoRepository.findAll();

            if (agendamentos.isEmpty()) {
                return RetornoComListaObjeto.<AgendamentoEntity>builder()
                        .statusCode(204)
                        .mensagem("Nenhum agendamento encontrado")
                        .objeto(agendamentos)
                        .build();
            }

            return RetornoComListaObjeto.<AgendamentoEntity>builder()
                    .statusCode(200)
                    .mensagem("Agendamentos encontrados com sucesso!")
                    .objeto(agendamentos)
                    .build();
        } catch (Exception e) {
            return RetornoComListaObjeto.<AgendamentoEntity>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar agendamentos: " + e.getMessage())
                    .objeto(List.of())
                    .build();
        }
    }

    public RetornoComListaObjeto<AgendamentoEntity> buscarAgendamentosByPessoaId(Long id) {
        try {

            List<AgendamentoEntity> agendamento = agendamentoRepository.findByVeiculo_Pessoa_Id(id);

            if (agendamento.isEmpty()) {
                return RetornoComListaObjeto.<AgendamentoEntity>builder()
                        .statusCode(204)
                        .mensagem("Nenhum agendamento encontrado!")
                        .objeto(agendamento)
                        .build();
            }

            return RetornoComListaObjeto.<AgendamentoEntity>builder()
                    .statusCode(200)
                    .mensagem("Agendamentos encontrados com sucesso!")
                    .objeto(agendamento)
                    .build();
        } catch (Exception e) {
            return RetornoComListaObjeto.<AgendamentoEntity>builder()
                    .statusCode(500)
                    .mensagem(e.getMessage())
                    .objeto(List.of())
                    .build();
        }
    }

    public RetornoSemObjeto atualizarAgendamento(Long id, AgendamentoDto agendamentoDto) {
        var retorno = new RetornoSemObjeto();

        try {
            var agendamento = agendamentoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

            if (agendamentoDto.getDataHora() != null) {
                var inicio = Date.from(agendamentoDto.getDataHora().atZone(ZoneId.systemDefault()).toInstant());
                var fim = Date.from(agendamentoDto.getDataHora().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());

                eventoGoogleCalendarService.atualizarEvento(
                        agendamento.getGoogleEventId(),
                        "Agendamento - " + agendamento.getVeiculo().getModelo(),
                        "Serviço para o veículo: " + agendamento.getVeiculo().getPlaca(),
                        inicio,
                        fim
                );

                agendamento.setDataHora(agendamentoDto.getDataHora());
            }

            if (agendamentoDto.getStatus() != null) {
                agendamento.setStatus(agendamentoDto.getStatus());
            }

            agendamentoRepository.save(agendamento);

           retorno.setStatusCode(200);
           retorno.setMensagem("Agendamento" + agendamento + "atualizado com sucesso!");
        } catch (Exception e) {
            retorno.setStatusCode(500);
            retorno.setMensagem(e.getMessage());
        }

        return retorno;
    }

    public RetornoSemObjeto deletarAgendamentos(Long id) {
        var retorno = new RetornoSemObjeto();
        try{
            var agendamento = agendamentoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Agendamento não encontrado!"));
            if (agendamento.getGoogleEventId() != null) {
                eventoGoogleCalendarService.deletarEvento(agendamento.getGoogleEventId());
            }

            agendamentoRepository.delete(agendamento);
            retorno.setStatusCode(200);
            retorno.setMensagem("Deleção do agendamento: " + id + ", realizada com sucesso!");
        } catch (Exception e) {
            retorno.setStatusCode(500);
            retorno.setMensagem(("Erro ao deletar agendamento: " + e.getMessage()));
        }
        return retorno;
    }
}
