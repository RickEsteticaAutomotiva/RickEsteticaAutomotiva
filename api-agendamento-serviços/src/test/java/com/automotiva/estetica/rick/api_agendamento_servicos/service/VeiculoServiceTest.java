package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.VeiculoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.VeiculoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.VeiculoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.VeiculoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock private PessoaRepository pessoaRepository;
    @Mock private VeiculoMapper veiculoMapper;
    @Mock private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    @Test
    @DisplayName("Deve retornar uma exception caso o usuário não seja encontrado na base")
    void cadastrarVeiculo_ExceptionTest() {
        VeiculoDto novoVeiculoReq = new VeiculoDto();
        novoVeiculoReq.setIdPessoa(1L);

        when(pessoaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradaException.class, () -> veiculoService.cadastrarVeiculo(novoVeiculoReq));
    }

    @Test
    @DisplayName("Deve cadastrar veículo com sucesso quando os dados estiverem corretos")
    void cadastrarVeiculo_sucesso() {
        Long idPessoa = 1L;
        PessoaEntity pessoa = new PessoaEntity();
        VeiculoEntity VeiculoEntity = new VeiculoEntity();
        VeiculoEntity salvo = new VeiculoEntity();
        VeiculoDto req = new VeiculoDto();
        req.setIdPessoa(idPessoa);

        VeiculoDto resposta = req;

        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(veiculoMapper.veiculoDtoParaVeiculo(req)).thenReturn(VeiculoEntity);
        when(veiculoMapper.veiculoParaVeiculoDto(VeiculoEntity)).thenReturn(resposta);

        VeiculoDto result = veiculoService.cadastrarVeiculo(req);

        assertNotNull(result);
        verify(veiculoRepository).save(VeiculoEntity);
    }


    @Test
    @DisplayName("Deve lançar exceção ao buscar todos quando nenhum veículo existir")
    void buscarTodosVeiculos_listaVazia() {
        when(veiculoRepository.findAll()).thenReturn(emptyList());

        assertThrows(RecursoNaoEncontradaException.class, () -> veiculoService.buscarTodosVeiculos());
    }

    @Test
    @DisplayName("Deve retornar lista de veículos quando existir ao menos um veículo")
    void buscarTodosVeiculos_sucesso() {
        VeiculoEntity entity = new VeiculoEntity();
        VeiculoDto dtoResposta = new VeiculoDto();

        when(veiculoRepository.findAll()).thenReturn(List.of(entity));
        when(veiculoMapper.veiculosParaVeiculosDto(List.of(entity)))
                .thenReturn(List.of(dtoResposta));

        List<VeiculoDto> result = veiculoService.buscarTodosVeiculos();

        assertEquals(1, result.size());
        verify(veiculoMapper).veiculosParaVeiculosDto(anyList());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pessoa não existir ao buscar veículos por ID")
    void buscarVeiculosByPessoaId_pessoaNaoEncontrada() {
        when(pessoaRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradaException.class,
                () -> veiculoService.buscarVeiculosByPessoaId(10L));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pessoa existe mas não possui veículos cadastrados")
    void buscarVeiculosByPessoaId_semVeiculos() {
        PessoaEntity pessoa = new PessoaEntity();
        pessoa.setId(5L);

        when(pessoaRepository.findById(5L)).thenReturn(Optional.of(pessoa));
        when(veiculoRepository.findByPessoa_Id(5L))
                .thenReturn(emptyList());

        assertThrows(RecursoNaoEncontradaException.class,
                () -> veiculoService.buscarVeiculosByPessoaId(5L));
    }

    @Test
    @DisplayName("Deve retornar lista de veículos da pessoa")
    void buscarVeiculosByPessoaId_sucesso() {
        PessoaEntity pessoa = new PessoaEntity();
        pessoa.setId(3L);
        VeiculoEntity VeiculoEntity = new VeiculoEntity();
        VeiculoDto dtoResposta = new VeiculoDto();

        when(pessoaRepository.findById(3L)).thenReturn(Optional.of(pessoa));
        when(veiculoRepository.findByPessoa_Id(3L)).thenReturn(List.of(VeiculoEntity));
        when(veiculoMapper.veiculosParaVeiculosDto(List.of(VeiculoEntity)))
                .thenReturn(List.of(dtoResposta));

        List<VeiculoDto> result = veiculoService.buscarVeiculosByPessoaId(3L);

        assertEquals(1, result.size());
        verify(veiculoRepository).findByPessoa_Id(3L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar veículo inexistente")
    void atualizarVeiculo_inexistente() {
        VeiculoDto dtoRequest = new VeiculoDto();

        when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradaException.class,
                () -> veiculoService.atualizarVeiculo(99L, dtoRequest));
    }

    @Test
    @DisplayName("Deve atualizar veículo com sucesso")
    void atualizarVeiculo_sucesso() {
        VeiculoDto dtoRequest = new VeiculoDto();
        VeiculoEntity VeiculoEntity = new VeiculoEntity();

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(VeiculoEntity));

        veiculoService.atualizarVeiculo(1L, dtoRequest);

        verify(veiculoMapper).atualizarVeiculoEntityFromDto(dtoRequest, VeiculoEntity);
        verify(veiculoRepository).save(VeiculoEntity);
    }
}
