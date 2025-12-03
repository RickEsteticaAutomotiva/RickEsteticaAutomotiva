package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.ItemServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ItemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ItemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ItemServicoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("itens não foram encontrados")
                    .detalhes("")
                    .build();
        }
        return itemServicoMapper.itemServicosParaItemServicosDto(itens);
    }

    public void criarItemServico(OrdemServicoDto ordemServicoRequest, OrdemServicoEntity ordemServicoSalva) {
        for (Long id : ordemServicoRequest.getServicos()) {
            ServicoEntity servico = servicoRepository.findById(id).get();
            ItemServicoEntity entity =
                    itemServicoMapper.ordemServicoParaItemServicoEntity(id, ordemServicoSalva, servico);
            itemServicoRepository.save(entity);
        }
    }

    public ItemServicoDto buscarPorId(Long id) {
        Optional<ItemServicoEntity> item = itemServicoRepository.findById(id);
        if (item.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("o item com id " + id + " não foi encontrado")
                    .detalhes("")
                    .build();
        }
        return itemServicoMapper.itemServicoParaItemServicoDto(item.get());
    }

    public ItemServicoDto atualizarItemServico(Long id, ItemServicoDto itemServicoDtoAtualizada) {
        Optional<ItemServicoEntity> existente = itemServicoRepository.findById(id);
        if (existente.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("o item com id" + id + "não foi encontrado")
                    .detalhes("")
                    .build();
        }
        ItemServicoEntity entity = existente.get();
        itemServicoMapper.atualizarItemServicoEntityFromDto(itemServicoDtoAtualizada, entity);
        itemServicoRepository.save(entity);
        return itemServicoMapper.itemServicoParaItemServicoDto(entity);
    }

    public void deletarItemServico(Long id) {
        Optional<ItemServicoEntity> item = itemServicoRepository.findById(id);
        if (item.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("o item com id" + id + "não foi encontrado")
                    .detalhes("")
                    .build();
        }
        itemServicoRepository.deleteById(id);
    }

    public List<ItemServicoDto> listarPorOrdem(Long idOrdem) {
        List<ItemServicoEntity> itens = itemServicoRepository.findByOrdemServicoId(idOrdem);
        return itemServicoMapper.itemServicosParaItemServicosDto(itens);
    }

    public List<Long> buscarServicosPorOrdemServicoId(Long ordemServicoId) {
        return itemServicoRepository.findByOrdemServicoId(ordemServicoId).stream()
                .map(item -> item.getServico().getId())
                .toList();
    }
}
