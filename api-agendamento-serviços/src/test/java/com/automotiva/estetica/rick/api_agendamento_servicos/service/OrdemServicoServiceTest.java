package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.OrdemServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private OrdemServicoMapper ordemServicoMapper;
    @Mock private ItemServicoService itemServicoService;

    @InjectMocks
    OrdemServicoService ordemServicoService;

    @Test
    @DisplayName("Deve retornar uma lista de ordens de serviço")
    void buscarPorUsuarioId() {
        Long idUsuario = 1L;

        OrdemServicoEntity entity = new OrdemServicoEntity();
        entity.setId(10L);

        OrdemServicoDto dto = new OrdemServicoDto();
        dto.setId(10L);

        when(ordemServicoRepository.findByVeiculo_Pessoa_Id(idUsuario)).thenReturn(List.of(entity));

        when(ordemServicoMapper.ordemServicoParaOrdemServicoDto(entity)).thenReturn(dto);

        when(itemServicoService.buscarServicosPorOrdemServicoId(dto.getId())).thenReturn(List.of(1L, 2L));

        List<OrdemServicoDto> resultado = ordemServicoService.buscarPorUsuarioId(idUsuario);

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve lançar uma exception quando a lista de ordens de serviço for vazia")
    void buscarPorUsuarioIdListaVazia() {
        Long idUsuario = 1L;

        when(ordemServicoRepository.findByVeiculo_Pessoa_Id(idUsuario)).thenReturn(Collections.emptyList());

        assertThrows(RecursoNaoEncontradaException.class, () -> ordemServicoService.buscarPorUsuarioId(idUsuario));
    }
}
