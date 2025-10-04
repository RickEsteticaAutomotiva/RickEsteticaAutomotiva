package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.DependenciaNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;

    public List<OrdemServicoDto> buscarTodos() {
        List<OrdemServicoEntity> ordemServicos = ordemServicoRepository.findAll();
        if (ordemServicos.isEmpty()) {
            throw new DependenciaNaoEncontradaException("Ordem de serviço");
        }
        return ordemServicos.stream()
                .map(this::converterParaDto)
                .collect(Collectors.toList());
    }

    public OrdemServicoDto criarOrdemServico(OrdemServicoDto ordemServico) {
        if (ordemServicoRepository.existsById(ordemServico.getId())) {
            throw new RecursoJaExisteException("Ordem de serviço");
        }
        OrdemServicoEntity ordemServicoEntity = converterEntity(ordemServico);
        ordemServicoRepository.save(ordemServicoEntity);
        return ordemServico;
    }

    public OrdemServicoDto buscarPorId(Long id) {
        Optional<OrdemServicoEntity> ordemServico = ordemServicoRepository.findById(id);
        if (ordemServico.isEmpty()) {
            throw new DependenciaNaoEncontradaException("Ordem de serviço");
        }
        return converterParaDto(ordemServico.get());
    }

    public OrdemServicoDto atualizarOrdemServico(Long id, OrdemServicoDto ordemServicoAtualizada) {
        Optional<OrdemServicoEntity> ordemServicoExistente = ordemServicoRepository.findById(id);
        if (ordemServicoExistente.isEmpty()) {
            throw new DependenciaNaoEncontradaException("Ordem de serviço");
        }
        OrdemServicoEntity ordemServico = ordemServicoExistente.get();
        atualizarOrdemServicoEntity(ordemServicoAtualizada, ordemServico);
        ordemServicoRepository.save(ordemServico);
        return ordemServicoAtualizada;
    }

    public void deletarOrdemServico(Long id) {
        Optional<OrdemServicoEntity> ordemServico = ordemServicoRepository.findById(id);
        if (ordemServico.isEmpty()) {
            throw new DependenciaNaoEncontradaException("Ordem de serviço");
        }
        ordemServicoRepository.deleteById(id);
    }

    private OrdemServicoDto converterParaDto(OrdemServicoEntity entity) {
        return OrdemServicoDto.builder()
                .id(entity.getId())
                .dtConclusao(entity.getDtConclusao())
                .observacoes(entity.getObservacoes())
                .status(entity.getStatus())
                .idAgendamento(entity.getIdAgendamento())
                .build();
    }

    public OrdemServicoEntity converterEntity(OrdemServicoDto dto) {
        return OrdemServicoEntity.builder()
                .dtConclusao(dto.getDtConclusao())
                .observacoes(dto.getObservacoes())
                .status(dto.getStatus())
                .idAgendamento(dto.getIdAgendamento())
                .build();
    }

    public void atualizarOrdemServicoEntity(OrdemServicoDto dto, OrdemServicoEntity entity) {
        entity.setObservacoes(dto.getObservacoes());
        entity.setStatus(dto.getStatus());
        entity.setDtConclusao(dto.getDtConclusao());
    }
}