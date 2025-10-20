package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CarinhoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.Carinho;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.CarinhoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarinhoService {
    private final CarinhoRepository carinhoRepository;
    private final PessoaRepository pessoaRepository;
    private final ServicoRepository servicoRepository;


    public Carinho adicionarCarinho(CarinhoDto carinhoDto) {
        PessoaEntity usuario = pessoaRepository.findById(carinhoDto.getIdPessoa())
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Usuário não encontrado: " + carinhoDto.getIdPessoa())
                        .detalhes("")
                        .build());
        ServicoEntity servico = servicoRepository.findById(carinhoDto.getIdServico())
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Serviço não encontrado: " + carinhoDto.getIdServico())
                        .detalhes("")
                        .build());

        if (carinhoRepository.existsByUsuarioAndServico(usuario, servico)) {
            throw RecursoJaExisteException.builder()
                    .mensagem("Esse servico já existe para este usuário.")
                    .detalhes("")
                    .build();
        }

        Carinho carinho = new Carinho();
        carinho.setUsuario(usuario);
        carinho.setServico(servico);

        return carinhoRepository.save(carinho);
    }

    public void removerCarinho(CarinhoDto carinhoDto) {
        PessoaEntity usuario = pessoaRepository.findById(carinhoDto.getIdServico())
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Usuário não encontrado: " + carinhoDto.getIdPessoa())
                        .detalhes("")
                        .build());
        ServicoEntity servico = servicoRepository.findById(carinhoDto.getIdServico())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado: " + carinhoDto.getIdServico()));


        if (!carinhoRepository.existsByUsuarioAndServico(usuario, servico)) {
            throw RecursoJaExisteException.builder()
                    .mensagem("Carinho não encontrado para este usuário e serviço")
                    .detalhes("")
                    .build();
        }

        carinhoRepository.deleteByUsuarioAndServico(usuario, servico);
    }
}
