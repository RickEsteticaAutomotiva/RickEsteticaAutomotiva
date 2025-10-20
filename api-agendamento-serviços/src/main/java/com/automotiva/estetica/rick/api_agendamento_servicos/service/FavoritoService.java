package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CarrinhoDto;
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

@Service
@RequiredArgsConstructor
public class FavoritoService {
    private final CarrinhoRepository carrinhoRepository;
    private final PessoaRepository pessoaRepository;
    private final ServicoRepository servicoRepository;


    public CarrinhoEntity adicionarCarinho(CarrinhoDto carrinhoDto) {
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
                    .mensagem("Esse servico já existe para este usuário.")
                    .detalhes("")
                    .build();
        }

        CarrinhoEntity carrinhoEntity = new CarrinhoEntity();
        carrinhoEntity.setPessoa(usuario);
        carrinhoEntity.setServico(servico);

        return carrinhoRepository.save(carrinhoEntity);
    }

    public void removerCarinho(CarrinhoDto carrinhoDto) {
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
}
