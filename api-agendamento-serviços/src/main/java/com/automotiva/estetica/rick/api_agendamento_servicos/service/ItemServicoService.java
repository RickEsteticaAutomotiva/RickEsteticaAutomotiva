package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ItemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ItemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.DependenciaNaoEncontradaException;
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

    public List<ItemServicoDto> buscarTodos() {
        List<ItemServicoEntity> itens = itemServicoRepository.findAll();
        if (itens.isEmpty()) {
            throw new DependenciaNaoEncontradaException("Item de serviço");
        }
        return itens.stream().map(this::converterParaDto).collect(Collectors.toList());
    }

    public ItemServicoDto criarItemServico(ItemServicoDto itemServicoDto) {
        if (itemServicoRepository.existsById(itemServicoDto.getId())) {
            throw new RecursoJaExisteException("Item de serviço");
        }
        ItemServicoEntity entity = converterEntity(itemServicoDto);
        itemServicoRepository.save(entity);
        return converterParaDto(entity);
    }

    public ItemServicoDto buscarPorId(Long id) {
        Optional<ItemServicoEntity> item = itemServicoRepository.findById(id);
        if (item.isEmpty()) {
            throw new DependenciaNaoEncontradaException("Item de serviço");
        }
        return converterParaDto(item.get());
    }

    public ItemServicoDto atualizarItemServico(Long id, ItemServicoDto itemServicoDtoAtualizada) {
        Optional<ItemServicoEntity> existente = itemServicoRepository.findById(id);
        if (existente.isEmpty()) {
            throw new DependenciaNaoEncontradaException("Item de serviço");
        }
        ItemServicoEntity entity = existente.get();
        atualizarItemServicoEntity(itemServicoDtoAtualizada, entity);
        itemServicoRepository.save(entity);
        return converterParaDto(entity);
    }

    public void deletarItemServico(Long id) {
        Optional<ItemServicoEntity> item = itemServicoRepository.findById(id);
        if (item.isEmpty()) {
            throw new DependenciaNaoEncontradaException("Item de serviço");
        }
        itemServicoRepository.deleteById(id);
    }

    private ItemServicoDto converterParaDto(ItemServicoEntity entity) {
        return ItemServicoDto.builder()
                .id(entity.getId())
                .preco(entity.getPreco())
                .idServico(entity.getServico().getId())
                .idOrdemServico(entity.getOrdemServico().getId())
                .build();
    }

    public ItemServicoEntity converterEntity(ItemServicoDto dto) {
        ServicoEntity servico = servicoRepository.findById(dto.getIdServico())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        OrdemServicoEntity ordem = ordemServicoRepository.findById(dto.getIdOrdemServico())
                .orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada"));
        return ItemServicoEntity.builder()
                .preco(dto.getPreco())
                .servico(servico)
                .ordemServico(ordem)
                .build();
    }

    public void atualizarItemServicoEntity(ItemServicoDto dto, ItemServicoEntity entity) {
        entity.setPreco(dto.getPreco());
    }

    public List<ItemServicoDto> listarPorOrdem(Long idOrdem) {
        List<ItemServicoEntity> itens = itemServicoRepository.findByOrdemServicoId(idOrdem);
        return itens.stream().map(this::converterParaDto).collect(Collectors.toList());
    }
}
