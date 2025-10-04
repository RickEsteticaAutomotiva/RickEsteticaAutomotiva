package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.OrdemServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoMapper ordemServicoMapper;

    public List<OrdemServicoDto> buscarTodos() {
        List<OrdemServicoEntity> ordemServicos = ordemServicoRepository.findAll();
        if (ordemServicos.isEmpty()) {
            throw new RecursoNaoEncontradaException("Ordem de serviço");
        }
        return ordemServicoMapper.ordemServicosParaOrdemServicosDto(ordemServicos);
    }

    public OrdemServicoDto criarOrdemServico(OrdemServicoDto ordemServico) {
        if (ordemServicoRepository.existsById(ordemServico.getId())) {
            throw new RecursoJaExisteException("Ordem de serviço");
        }
        OrdemServicoEntity ordemServicoEntity = ordemServicoMapper.ordemServicoDtoParaOrdemServico(ordemServico);
        ordemServicoRepository.save(ordemServicoEntity);
        return ordemServicoMapper.ordemServicoParaOrdemServicoDto(ordemServicoEntity);
    }

    public OrdemServicoDto buscarPorId(Long id) {
        Optional<OrdemServicoEntity> ordemServico = ordemServicoRepository.findById(id);
        if (ordemServico.isEmpty()) {
            throw new RecursoNaoEncontradaException("Ordem de serviço");
        }
        return ordemServicoMapper.ordemServicoParaOrdemServicoDto(ordemServico.get());
    }

    public OrdemServicoDto atualizarOrdemServico(Long id, OrdemServicoDto ordemServicoAtualizada) {
        Optional<OrdemServicoEntity> ordemServicoExistente = ordemServicoRepository.findById(id);
        if (ordemServicoExistente.isEmpty()) {
            throw new RecursoNaoEncontradaException("Ordem de serviço");
        }
        OrdemServicoEntity ordemServico = ordemServicoExistente.get();
        ordemServicoMapper.atualizarOrdemServicoEntityFromDto(ordemServicoAtualizada, ordemServico);
        ordemServicoRepository.save(ordemServico);
        return ordemServicoMapper.ordemServicoParaOrdemServicoDto(ordemServico);
    }

    public void deletarOrdemServico(Long id) {
        Optional<OrdemServicoEntity> ordemServico = ordemServicoRepository.findById(id);
        if (ordemServico.isEmpty()) {
            throw new RecursoNaoEncontradaException("Ordem de serviço");
        }
        ordemServicoRepository.deleteById(id);
    }
}