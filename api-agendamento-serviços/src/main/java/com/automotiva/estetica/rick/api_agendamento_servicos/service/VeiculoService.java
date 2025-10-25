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
                .orElseThrow(() ->
                        RecursoNaoEncontradaException.builder()
                                .mensagem("a pessoa com id " + veiculoDto.getIdPessoa() + " não foi encontrada")
                                .detalhes("")
                                .build());

        VeiculoEntity novoVeiculo = veiculoMapper.veiculoDtoParaVeiculo(veiculoDto);
        novoVeiculo.setPessoa(pessoa);
        veiculoRepository.save(novoVeiculo);

        return veiculoMapper.veiculoParaVeiculoDto(novoVeiculo);
    }

    public List<VeiculoDto> buscarTodosVeiculos() {
        List<VeiculoEntity> veiculos = veiculoRepository.findAll();
        if (veiculos.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("nenhum veículo foi encontrado")
                    .detalhes("")
                    .build();
        }
        return veiculoMapper.veiculosParaVeiculosDto(veiculos);
    }

    public List<VeiculoDto> buscarVeiculosByPessoaId(Long idPessoa) {
        PessoaEntity donoVeiculo = pessoaRepository.findById(idPessoa)
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("nenhum veículo foi encontrado para a pessoa " + idPessoa)
                        .detalhes("")
                        .build());

        List<VeiculoEntity> veiculos = veiculoRepository.findByPessoa_Id(donoVeiculo.getId());

        if (veiculos.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("veículo não foi encontrado")
                    .detalhes("")
                    .build();
        }

        return veiculoMapper.veiculosParaVeiculosDto(veiculos);
    }

    public void atualizarVeiculo(VeiculoDto veiculoDto) {
        var veiculo = veiculoRepository.findByPlaca(veiculoDto.getPlaca())
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                .mensagem("o veículo com a placa " + veiculoDto.getPlaca() + " não foi encontrado")
                .detalhes("")
                .build());

        if (veiculo.getCor() != null) {
            veiculo.setCor(veiculoDto.getCor());
        }

        veiculoRepository.save(veiculo);
    }

    public void deletarVeiculo(Long id) {
        if (veiculoRepository.existsById(id)) {
            veiculoRepository.deleteById(id);
        } else {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("o veículo com id " + id + " não foi encontrado")
                    .detalhes("")
                    .build();
        }
    }
}
