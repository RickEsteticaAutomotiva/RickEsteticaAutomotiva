package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.VeiculoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.VeiculoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.DependenciaNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final PessoaRepository pessoaRepository;

    public VeiculoDto cadastrarVeiculo(VeiculoDto veiculoDto) {
        PessoaEntity pessoa = pessoaRepository.findById(veiculoDto.getIdPessoa())
                .orElseThrow(() -> new DependenciaNaoEncontradaException("Pessoa"));

        VeiculoEntity novoVeiculo = VeiculoEntity.builder()
                .placa(veiculoDto.getPlaca())
                .modelo(veiculoDto.getModelo())
                .marca(veiculoDto.getMarca())
                .porte(veiculoDto.getPorte())
                .cor(veiculoDto.getCor())
                .ano(veiculoDto.getAno())
                .pessoa(pessoa)
                .build();

        veiculoRepository.save(novoVeiculo);

        return converterParaDto(novoVeiculo);
    }

    public List<VeiculoDto> buscarTodosVeiculos() {
        List<VeiculoEntity> veiculos = veiculoRepository.findAll();
        if (veiculos.isEmpty()) {
            throw new DependenciaNaoEncontradaException("Veículo");
        }
        return veiculos.stream()
                .map(this::converterParaDto)
                .collect(Collectors.toList());
    }

    public List<VeiculoDto> buscarVeiculosByPessoaId(Long idPessoa) {
        PessoaEntity donoVeiculo = pessoaRepository.findById(idPessoa)
                .orElseThrow(() -> new DependenciaNaoEncontradaException("Pessoa"));
        List<VeiculoEntity> veiculos = veiculoRepository.findByPessoa_Id(donoVeiculo.getId());
        if (veiculos.isEmpty()) {
            throw new DependenciaNaoEncontradaException("Veículo");
        }
        return veiculos.stream()
                .map(this::converterParaDto)
                .collect(Collectors.toList());
    }

    public void deletarVeiculo(Long id) {
        if (veiculoRepository.existsById(id)) {
            veiculoRepository.deleteById(id);
        } else {
            throw new DependenciaNaoEncontradaException("Veículo");
        }
    }

    private VeiculoDto converterParaDto(VeiculoEntity entity) {
        return VeiculoDto.builder()
                .id(entity.getId())
                .placa(entity.getPlaca())
                .modelo(entity.getModelo())
                .marca(entity.getMarca())
                .porte(entity.getPorte())
                .cor(entity.getCor())
                .ano(entity.getAno())
                .idPessoa(entity.getPessoa().getId())
                .build();
    }
}
