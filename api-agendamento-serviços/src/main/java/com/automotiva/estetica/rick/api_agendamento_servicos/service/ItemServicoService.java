package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.ItemServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ItemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ItemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ItemServicoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServicoService {

    private final ItemServicoRepository itemServicoRepository;
    private final ServicoRepository servicoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final ItemServicoMapper itemServicoMapper;

    public List<ItemServicoDto> buscarTodos() {
        List<ItemServicoEntity> itens = itemServicoRepository.findAll();
        if (itens.isEmpty()) {
            throw new RecursoNaoEncontradaException("Item de serviço");
        }
        return itemServicoMapper.itemServicosParaItemServicosDto(itens);
    }

    public ItemServicoDto criarItemServico(ItemServicoDto itemServicoDto) {
        if (itemServicoRepository.existsById(itemServicoDto.getId())) {
            throw new RecursoJaExisteException("Item de serviço");
        }
        ItemServicoEntity entity = converterEntity(itemServicoDto);
        itemServicoRepository.save(entity);
        return itemServicoMapper.itemServicoParaItemServicoDto(entity);
    }

    public ItemServicoDto buscarPorId(Long id) {
        Optional<ItemServicoEntity> item = itemServicoRepository.findById(id);
        if (item.isEmpty()) {
            throw new RecursoNaoEncontradaException("Item de serviço");
        }
        return itemServicoMapper.itemServicoParaItemServicoDto(item.get());
    }

    public ItemServicoDto atualizarItemServico(Long id, ItemServicoDto itemServicoDtoAtualizada) {
        Optional<ItemServicoEntity> existente = itemServicoRepository.findById(id);
        if (existente.isEmpty()) {
            throw new RecursoNaoEncontradaException("Item de serviço");
        }
        ItemServicoEntity entity = existente.get();
        itemServicoMapper.atualizarItemServicoEntityFromDto(itemServicoDtoAtualizada, entity);
        itemServicoRepository.save(entity);
        return itemServicoMapper.itemServicoParaItemServicoDto(entity);
    }

    public void deletarItemServico(Long id) {
        Optional<ItemServicoEntity> item = itemServicoRepository.findById(id);
        if (item.isEmpty()) {
            throw new RecursoNaoEncontradaException("Item de serviço");
        }
        itemServicoRepository.deleteById(id);
    }

    public ItemServicoEntity converterEntity(ItemServicoDto dto) {
        ServicoEntity servico = servicoRepository.findById(dto.getIdServico())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        OrdemServicoEntity ordem = ordemServicoRepository.findById(dto.getIdOrdemServico())
                .orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada"));

        ItemServicoEntity entity = itemServicoMapper.itemServicoDtoParaItemServico(dto);
        entity.setServico(servico);
        entity.setOrdemServico(ordem);
        return entity;
    }

    public List<ItemServicoDto> listarPorOrdem(Long idOrdem) {
        List<ItemServicoEntity> itens = itemServicoRepository.findByOrdemServicoId(idOrdem);
        return itemServicoMapper.itemServicosParaItemServicosDto(itens);
    }
}