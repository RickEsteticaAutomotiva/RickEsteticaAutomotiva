package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.VeiculoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.VeiculoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComListaObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoSemObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeiculoService {
    @Autowired
    VeiculoRepository veiculoRepository;
    @Autowired
    PessoaRepository pessoaRepository;

    public RetornoSemObjeto cadastrarVeiculo(VeiculoDto veiculoDto) {
        var retorno = new RetornoSemObjeto();

        try {
            PessoaEntity pessoa = pessoaRepository.findById(veiculoDto.getPessoa())
                    .orElseThrow(() -> new RuntimeException("Pessoa não encontrada, não é possível registrar o veículo!"));

            var novoVeiculo = VeiculoEntity.builder()
                    .placa(veiculoDto.getPlaca())
                    .modelo(veiculoDto.getModelo())
                    .marca(veiculoDto.getMarca())
                    .porte(veiculoDto.getPorte())
                    .cor(veiculoDto.getCor())
                    .ano(veiculoDto.getAno())
                    .pessoa(pessoa)
                    .build();

            veiculoRepository.save(novoVeiculo);

            retorno.setStatusCode(201);
            retorno.setMensagem("Veículo cadastrado com sucesso!");
        } catch (Exception e) {
            retorno.setStatusCode(500);
            retorno.setMensagem("Erro ao cadastrar veículo - " + e.getMessage());
        }

        return retorno;
    }

    public RetornoComListaObjeto buscarTodosVeiculos() {

        try {
            List<VeiculoEntity> veiculos = veiculoRepository.findAll();

            if (veiculos.isEmpty()) {
                return RetornoComListaObjeto.<VeiculoEntity>builder()
                        .statusCode(204)
                        .mensagem("Nenhum veículo encontrado")
                        .objeto(veiculos)
                        .build();
            }

            return RetornoComListaObjeto.<VeiculoEntity>builder()
                    .statusCode(200)
                    .mensagem("Veículo encontrado com sucesso!")
                    .objeto(veiculos)
                    .build();
        } catch (Exception e) {
            return RetornoComListaObjeto.<VeiculoEntity>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar veículos: " + e.getMessage())
                    .objeto(List.of())
                    .build();
        }
    }

    public RetornoComListaObjeto buscarVeiculosByPessoaId(Long idPessoa) {
        try {
            PessoaEntity donoVeiculo = pessoaRepository.findById(idPessoa).get();
            List<VeiculoEntity> veiculos = veiculoRepository.findByPessoa_Id(donoVeiculo.getId());

            if (veiculos.isEmpty()) {
                return RetornoComListaObjeto.<VeiculoEntity>builder()
                        .statusCode(204)
                        .mensagem("Nenhum veículo encontrado para o id - " + donoVeiculo.getId())
                        .objeto(List.of())
                        .build();
            }

            return RetornoComListaObjeto.<VeiculoEntity>builder()
                    .statusCode(200)
                    .mensagem("Veículos encontrados!")
                    .objeto(veiculos)
                    .build();
        } catch (Exception e) {
            return RetornoComListaObjeto.<VeiculoEntity>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar veículos por id - " + e.getMessage())
                    .objeto(List.of())
                    .build();
        }
    }

    public RetornoSemObjeto deletarVeiculo(Long id) {
        var retorno = new RetornoSemObjeto();

        try {
            if (veiculoRepository.existsById(id)) {
                veiculoRepository.deleteById(id);
                retorno.setStatusCode(200);
                retorno.setMensagem("Veículo deletado com sucesso!");
            } else {
                retorno.setStatusCode(404);
                retorno.setMensagem("Veículo não encontrado!");
            }
        } catch (Exception e) {
            retorno.setStatusCode(500);
            retorno.setMensagem("Erro ao deletar veículo" + e.getMessage());
        }

        return retorno;
    }
}
