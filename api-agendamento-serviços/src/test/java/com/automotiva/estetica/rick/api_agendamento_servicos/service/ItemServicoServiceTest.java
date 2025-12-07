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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServicoServiceTest {

    @Mock private ItemServicoRepository itemServicoRepository;
    @Mock private ServicoRepository servicoRepository;
    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private ItemServicoMapper itemServicoMapper;

    @InjectMocks
    private ItemServicoService itemServicoService;

    @Test
    @DisplayName("Deve lançar exceção ao buscar todos quando a lista estiver vazia")
    void buscarTodos_ListaVazia_Exception() {

        when(itemServicoRepository.findAll()).thenReturn(emptyList());

        assertThrows(RecursoNaoEncontradaException.class,
                () -> itemServicoService.buscarTodos());
    }


    @Test
    @DisplayName("Deve retornar lista de itens quando existirem elementos")
    void buscarTodos_Sucesso() {

        ItemServicoEntity entity = new ItemServicoEntity();
        ItemServicoDto dto = new ItemServicoDto();

        when(itemServicoRepository.findAll()).thenReturn(List.of(entity));
        when(itemServicoMapper.itemServicosParaItemServicosDto(anyList()))
                .thenReturn(List.of(dto));

        List<ItemServicoDto> resposta = itemServicoService.buscarTodos();

        assertEquals(1, resposta.size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar item inexistente")
    void buscarPorId_Inexistente_Exception() {

        when(itemServicoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradaException.class,
                () -> itemServicoService.buscarPorId(1L));
    }

    @Test
    @DisplayName("Deve retornar item buscado por ID")
    void buscarPorId_Sucesso() {

        ItemServicoEntity entity = new ItemServicoEntity();
        ItemServicoDto dto = new ItemServicoDto();

        when(itemServicoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(itemServicoMapper.itemServicoParaItemServicoDto(entity)).thenReturn(dto);

        ItemServicoDto resposta = itemServicoService.buscarPorId(1L);

        assertNotNull(resposta);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar item inexistente")
    void atualizarItemServico_Inexistente_Exception() {

        when(itemServicoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradaException.class,
                () -> itemServicoService.atualizarItemServico(1L, new ItemServicoDto()));
    }

    @Test
    @DisplayName("Deve atualizar item com sucesso")
    void atualizarItemServico_Sucesso() {

        ItemServicoEntity entity = new ItemServicoEntity();
        ItemServicoDto dto = new ItemServicoDto();

        when(itemServicoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(itemServicoMapper.itemServicoParaItemServicoDto(entity)).thenReturn(dto);

        ItemServicoDto resposta = itemServicoService.atualizarItemServico(1L, dto);

        verify(itemServicoMapper).atualizarItemServicoEntityFromDto(dto, entity);
        verify(itemServicoRepository).save(entity);

        assertNotNull(resposta);
    }

    @Test
    @DisplayName("Deve retornar lista de itens ao listar por ordem")
    void listarPorOrdem_Sucesso() {

        ItemServicoEntity entity = new ItemServicoEntity();
        ItemServicoDto dto = new ItemServicoDto();

        when(itemServicoRepository.findByOrdemServicoId(10L)).thenReturn(List.of(entity));
        when(itemServicoMapper.itemServicosParaItemServicosDto(anyList()))
                .thenReturn(List.of(dto));

        List<ItemServicoDto> resposta = itemServicoService.listarPorOrdem(10L);

        assertEquals(1, resposta.size());
    }

    @Test
    @DisplayName("Deve retornar IDs de serviços vinculados à ordem")
    void buscarServicosPorOrdemServicoId_Sucesso() {

        ServicoEntity servico = new ServicoEntity();
        servico.setId(5L);

        ItemServicoEntity item = new ItemServicoEntity();
        item.setServico(servico);

        when(itemServicoRepository.findByOrdemServicoId(7L)).thenReturn(List.of(item));

        List<Long> ids = itemServicoService.buscarServicosPorOrdemServicoId(7L);

        assertEquals(1, ids.size());
        assertEquals(5L, ids.getFirst());
    }

    @Test
    @DisplayName("Deve criar itens de serviço para cada serviço da ordem")
    void criarItemServico_Sucesso() {

        OrdemServicoDto request = new OrdemServicoDto();
        request.setServicos(List.of(1L, 2L));

        OrdemServicoEntity ordemSalva = new OrdemServicoEntity();

        ServicoEntity servico1 = new ServicoEntity();
        ServicoEntity servico2 = new ServicoEntity();

        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico1));
        when(servicoRepository.findById(2L)).thenReturn(Optional.of(servico2));

        when(itemServicoMapper.ordemServicoParaItemServicoEntity(eq(1L), eq(ordemSalva), eq(servico1)))
                .thenReturn(new ItemServicoEntity());

        when(itemServicoMapper.ordemServicoParaItemServicoEntity(eq(2L), eq(ordemSalva), eq(servico2)))
                .thenReturn(new ItemServicoEntity());

        itemServicoService.criarItemServico(request, ordemSalva);

        verify(itemServicoRepository, times(2)).save(any(ItemServicoEntity.class));
    }
}
