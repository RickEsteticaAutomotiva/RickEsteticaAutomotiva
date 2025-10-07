package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.VeiculoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.VeiculoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.VeiculoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final PessoaRepository pessoaRepository;
    private final VeiculoMapper veiculoMapper;

    public VeiculoDto cadastrarVeiculo(VeiculoDto veiculoDto) {
        PessoaEntity pessoa = pessoaRepository.findById(veiculoDto.getIdPessoa())
                .orElseThrow(() -> new RecursoNaoEncontradaException("Pessoa"));

        VeiculoEntity novoVeiculo = veiculoMapper.veiculoDtoParaVeiculo(veiculoDto);
        novoVeiculo.setPessoa(pessoa);
        veiculoRepository.save(novoVeiculo);

        return veiculoMapper.veiculoParaVeiculoDto(novoVeiculo);
    }

    public List<VeiculoDto> buscarTodosVeiculos() {
        List<VeiculoEntity> veiculos = veiculoRepository.findAll();
        if (veiculos.isEmpty()) {
            throw new RecursoNaoEncontradaException("Veículo");
        }
        return veiculoMapper.veiculosParaVeiculosDto(veiculos);
    }

    public List<VeiculoDto> buscarVeiculosByPessoaId(Long idPessoa) {
        PessoaEntity donoVeiculo = pessoaRepository.findById(idPessoa)
                .orElseThrow(() -> new RecursoNaoEncontradaException("Pessoa"));
        List<VeiculoEntity> veiculos = veiculoRepository.findByPessoa_Id(donoVeiculo.getId());
        if (veiculos.isEmpty()) {
            throw new RecursoNaoEncontradaException("Veículo");
        }
        return veiculoMapper.veiculosParaVeiculosDto(veiculos);
    }

    public void deletarVeiculo(Long id) {
        if (veiculoRepository.existsById(id)) {
            veiculoRepository.deleteById(id);
        } else {
            throw new RecursoNaoEncontradaException("Veículo");
        }
    }
}
