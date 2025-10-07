package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.ServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;

    public List<ServicoDto> buscarTodos() {
        List<ServicoEntity> servicos = servicoRepository.findAll();
        if (servicos.isEmpty()) {
            throw new RecursoNaoEncontradaException("Serviço");
        }
        return servicoMapper.servicosParaServicosDto(servicos);
    }

    public ServicoDto criarServico(ServicoDto servico) {
        if (servicoRepository.existsById(servico.getId())) {
            throw new RecursoJaExisteException("Serviço");
        }
        ServicoEntity servicoEntity = servicoMapper.servicoDtoParaServico(servico);
        servicoRepository.save(servicoEntity);
        return servicoMapper.servicoParaServicoDto(servicoEntity);
    }

    public ServicoDto buscarPorId(Long id) {
        Optional<ServicoEntity> servico = servicoRepository.findById(id);
        if (servico.isEmpty()) {
            throw new RecursoNaoEncontradaException("Serviço");
        }
        return servicoMapper.servicoParaServicoDto(servico.get());
    }

    public ServicoDto atualizarServico(Long id, ServicoDto servicoAtualizada) {
        Optional<ServicoEntity> servicoExistente = servicoRepository.findById(id);
        if (servicoExistente.isEmpty()) {
            throw new RecursoNaoEncontradaException("Serviço");
        }
        ServicoEntity servico = servicoExistente.get();
        servicoMapper.atualizarServicoEntityFromDto(servicoAtualizada, servico);
        servicoRepository.save(servico);
        return servicoMapper.servicoParaServicoDto(servico);
    }

    public void deletarServico(Long id) {
        Optional<ServicoEntity> servico = servicoRepository.findById(id);
        if (servico.isEmpty()) {
            throw new RecursoNaoEncontradaException("Serviço");
        }
        servicoRepository.deleteById(id);
    }
}