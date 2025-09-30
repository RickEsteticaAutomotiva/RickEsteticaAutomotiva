// src/main/java/com/automotiva/estetica/rick/api_agendamento_servicos/service/ServicoService.java
package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public List<ServicoDto> buscarTodos() {
        List<ServicoEntity> servicos = servicoRepository.findAll();
        if (servicos.isEmpty()) {
            throw new RuntimeException("Nenhum serviço encontrado.");
        }
        return servicos.stream()
                .map(this::converterParaDto)
                .collect(Collectors.toList());
    }

    public ServicoDto criarServico(ServicoDto servico) {
        if (servicoRepository.existsById(servico.getId())) {
            throw new RuntimeException("Serviço já cadastrado.");
        }
        ServicoEntity servicoEntity = converterEntity(servico);
        servicoRepository.save(servicoEntity);
        return servico;
    }

    public ServicoDto buscarPorId(Long id) {
        Optional<ServicoEntity> servico = servicoRepository.findById(id);
        if (servico.isEmpty()) {
            throw new RuntimeException("Serviço não encontrado.");
        }
        return converterParaDto(servico.get());
    }

    public ServicoDto atualizarServico(Long id, ServicoDto servicoAtualizada) {
        Optional<ServicoEntity> servicoExistente = servicoRepository.findById(id);
        if (servicoExistente.isEmpty()) {
            throw new RuntimeException("Serviço não encontrado.");
        }
        ServicoEntity servico = servicoExistente.get();
        atualizarServicoEntity(servicoAtualizada, servico);
        servicoRepository.save(servico);
        return servicoAtualizada;
    }

    public void deletarServico(Long id) {
        Optional<ServicoEntity> servico = servicoRepository.findById(id);
        if (servico.isEmpty()) {
            throw new RuntimeException("Serviço não encontrado.");
        }
        servicoRepository.deleteById(id);
    }

    private ServicoDto converterParaDto(ServicoEntity entity) {
        return ServicoDto.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .preco(entity.getPreco())
                .build();
    }

    public ServicoEntity converterEntity(ServicoDto dto) {
        return ServicoEntity.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .preco(dto.getPreco())
                .build();
    }

    public void atualizarServicoEntity(ServicoDto dto, ServicoEntity entity) {
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setPreco(dto.getPreco());
    }
}
