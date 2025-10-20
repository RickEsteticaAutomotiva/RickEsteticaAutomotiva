package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.CarrinhoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.ServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CarrinhoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.CarrinhoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.CarrinhoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarrinhoService {
    private final CarrinhoRepository carrinhoRepository;
    private final PessoaRepository pessoaRepository;
    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;
    private final CarrinhoMapper carrinhoMapper;


    public CarrinhoEntity adicionarCarrinho(CarrinhoDto carrinhoDto) {
        PessoaEntity usuario = pessoaRepository.findById(carrinhoDto.getIdPessoa())
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Usuário não encontrado: " + carrinhoDto.getIdPessoa())
                        .detalhes("")
                        .build());
        ServicoEntity servico = servicoRepository.findById(carrinhoDto.getIdServico())
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Serviço não encontrado: " + carrinhoDto.getIdServico())
                        .detalhes("")
                        .build());

        if (carrinhoRepository.existsByPessoaAndServico(usuario, servico)) {
            throw RecursoJaExisteException.builder()
                    .mensagem("Esse carrinho já existe para este usuário.")
                    .detalhes("")
                    .build();
        }

        CarrinhoEntity carrinhoEntity = carrinhoMapper.carrinhoDtoParaEntity(carrinhoDto);
        return carrinhoRepository.save(carrinhoEntity);
    }

    public void removerCarrinho(CarrinhoDto carrinhoDto) {
        PessoaEntity usuario = pessoaRepository.findById(carrinhoDto.getIdServico())
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Usuário não encontrado: " + carrinhoDto.getIdPessoa())
                        .detalhes("")
                        .build());
        ServicoEntity servico = servicoRepository.findById(carrinhoDto.getIdServico())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado: " + carrinhoDto.getIdServico()));


        if (!carrinhoRepository.existsByPessoaAndServico(usuario, servico)) {
            throw RecursoJaExisteException.builder()
                    .mensagem("Carinho não encontrado para este usuário e serviço")
                    .detalhes("")
                    .build();
        }

        carrinhoRepository.deleteByPessoaAndServico(usuario, servico);
    }

    public List<ServicoDto> listarServicosPessoa(Long idPessoa) {
        PessoaEntity usuario = pessoaRepository.findById(idPessoa)
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Usuário não encontrado: " + idPessoa)
                        .detalhes("")
                        .build());

        List<CarrinhoEntity> itens = carrinhoRepository.findByPessoa(usuario);
        List<ServicoEntity> servicos = itens.stream()
                .map(CarrinhoEntity::getServico)
                .collect(Collectors.toList());

        return servicoMapper.servicosParaServicosDto(servicos);
    }

}
