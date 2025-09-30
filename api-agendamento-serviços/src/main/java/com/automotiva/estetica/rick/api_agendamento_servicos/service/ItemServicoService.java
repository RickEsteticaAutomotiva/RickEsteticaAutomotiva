package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ItemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ItemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComListaObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoSemObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ItemServicoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;

import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServicoService {
    @Autowired
    ItemServicoRepository itemServicoRepository;
    @Autowired
    ServicoRepository servicoRepository;
    @Autowired
    OrdemServicoRepository ordemServicoRepository;

    public RetornoComListaObjeto<ItemServicoEntity> buscarTodos() {

        try {
            List<ItemServicoEntity> itemServicoDto = itemServicoRepository.findAll();

            if (itemServicoDto.isEmpty()) {
                return RetornoComListaObjeto.<ItemServicoEntity>builder()
                        .statusCode(404)
                        .mensagem("Nenhuma item ServicoDtoviço encontrada.")
                        .objeto(List.of())
                        .build();
            }

            return RetornoComListaObjeto.<ItemServicoEntity>builder()
                    .statusCode(200)
                    .mensagem("Item de serviço encontradas com sucesso.")
                    .objeto(itemServicoDto)
                    .build();

        } catch (Exception e) {
            return RetornoComListaObjeto.<ItemServicoEntity>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar itemServicoDto: " + e.getMessage())
                    .objeto(List.of())
                    .build();
        }
    }

    public RetornoSemObjeto criarItemServico(ItemServicoDto itemServicoDto) {
        try {

            if (itemServicoRepository.existsById(itemServicoDto.getId())) {
                return RetornoSemObjeto.builder()
                        .statusCode(400)
                        .mensagem("Item de serviço já cadastrada.")
                        .build();
            }

            var ItemServicoEntity = converterEntity(itemServicoDto);
//            ItemServicoEntity.setObservacoes(itemServicoDto.());
            itemServicoRepository.save(ItemServicoEntity);

            return RetornoSemObjeto.builder()
                    .statusCode(201)
                    .mensagem("Item de serviço criada com sucesso.")
                    .build();

        } catch (Exception e) {
            return RetornoSemObjeto.builder()
                    .statusCode(500)
                    .mensagem("Erro ao criar Item de serviço: " + e.getMessage())
                    .build();
        }
    }

    public RetornoComObjeto<ItemServicoDto> buscarPorId(Long id) {
        try {
            Optional<ItemServicoEntity> itemServicoDto = itemServicoRepository.findById(id);

            if (itemServicoDto.isEmpty()) {
                return RetornoComObjeto.<ItemServicoDto>builder()
                        .statusCode(404)
                        .mensagem("Item de serviço não encontrada.")
                        .objeto(null)
                        .build();
            }

            return RetornoComObjeto.<ItemServicoDto>builder()
                    .statusCode(200)
                    .mensagem("Item de serviço encontrada com sucesso.")
                    .objeto(converterParaDto(itemServicoDto.get()))
                    .build();

        } catch (Exception e) {
            return RetornoComObjeto.<ItemServicoDto>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar Item de serviço: " + e.getMessage())
                    .objeto(null)
                    .build();
        }
    }

    public RetornoComObjeto<ItemServicoDto> atualizarItemServico(Long id, ItemServicoDto itemServicoDtoAtualizada) {
        try {
            Optional<ItemServicoEntity> itemServicoDtoExistente = itemServicoRepository.findById(id);

            if (itemServicoDtoExistente.isEmpty()) {
                return RetornoComObjeto.<ItemServicoDto>builder()
                        .statusCode(404)
                        .mensagem("Item de Serviço não encontrada.")
                        .objeto(null)
                        .build();
            }

            ItemServicoEntity itemServicoDto = itemServicoDtoExistente.get();
            atualizarItemServicoEntity(itemServicoDtoAtualizada, itemServicoDto);

            itemServicoRepository.save(itemServicoDto);

            return RetornoComObjeto.<ItemServicoDto>builder()
                    .statusCode(200)
                    .mensagem("Item de Serviço atualizada com sucesso.")
                    .objeto(itemServicoDtoAtualizada)
                    .build();

        } catch (Exception e) {
            return RetornoComObjeto.<ItemServicoDto>builder()
                    .statusCode(500)
                    .mensagem("Erro ao atualizar Item de Serviço: " + e.getMessage())
                    .objeto(null)
                    .build();
        }
    }

    public RetornoSemObjeto deletarItemServico(Long id) {
        try {
            Optional<ItemServicoEntity> itemServicoDto = itemServicoRepository.findById(id);

            if (itemServicoDto.isEmpty()) {
                return RetornoSemObjeto.builder()
                        .statusCode(404)
                        .mensagem("Item de Servico não encontrada.")
                        .build();
            }

            itemServicoRepository.deleteById(id);

            return RetornoSemObjeto.builder()
                    .statusCode(200)
                    .mensagem("Item de Servico deletada com sucesso.")
                    .build();

        } catch (Exception e) {
            return RetornoSemObjeto.builder()
                    .statusCode(500)
                    .mensagem("Erro ao deletar itemServicoDtovico: " + e.getMessage())
                    .build();
        }
    }

    private ItemServicoDto converterParaDto(ItemServicoEntity entity) {
        return ItemServicoDto.builder()
                .id(entity.getId())
                .preco(entity.getPreco())
                .idServico(entity.getServico().getId())
                .idOrdemServico(entity.getOrdemServico().getId())
                .build();
    }
//
//    private List<ItemServicoDto> converterListaParaDto(List<ItemServicoEntity> entities) {
//        return entities.stream()
//                .map(this::converterParaDto)
//                .collect(Collectors.toList());
//    }
//
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
