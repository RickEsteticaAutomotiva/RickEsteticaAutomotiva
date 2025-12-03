package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.CarrinhoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.ServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CarrinhoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoCarrinhoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.CarrinhoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.CarrinhoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarrinhoService {
    private final CarrinhoRepository carrinhoRepository;
    private final PessoaRepository pessoaRepository;
    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;
    private final CarrinhoMapper carrinhoMapper;

    public void adicionarCarrinho(CarrinhoDto carrinhoDto) {
        PessoaEntity usuario = pessoaRepository
                .findById(carrinhoDto.getIdPessoa())
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Usuário não encontrado: " + carrinhoDto.getIdPessoa())
                        .detalhes("")
                        .build());
        ServicoEntity servico = servicoRepository
                .findById(carrinhoDto.getIdServico())
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
        carrinhoRepository.save(carrinhoEntity);
    }

    @Transactional
    public void removerCarrinho(Long idCarrinho) {
        carrinhoRepository.findById(idCarrinho).orElseThrow(() -> RecursoNaoEncontradaException.builder()
                .mensagem("Carrinho não encontrado para este usuário.")
                .detalhes("")
                .build());

        carrinhoRepository.deleteById(idCarrinho);
    }

    @Transactional
    public void limparCarrinhoPessoa(Long idPessoa) {
        PessoaEntity pessoa = pessoaRepository
                .findById(idPessoa)
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Usuário não encontrado: " + idPessoa)
                        .detalhes("")
                        .build());

        List<CarrinhoEntity> itens = carrinhoRepository.findByPessoaId(pessoa.getId());

        if (itens == null || itens.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("Carrinho não encontrado para este usuário.")
                    .detalhes("")
                    .build();
        }

        carrinhoRepository.deleteAll(itens);
    }

    public List<ServicoCarrinhoDto> listarCarrinhoPessoa(Long idPessoa) {
        PessoaEntity pessoa = pessoaRepository
                .findById(idPessoa)
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Usuário não encontrado: " + idPessoa)
                        .detalhes("")
                        .build());

        List<CarrinhoEntity> itens = carrinhoRepository.findByPessoaId(pessoa.getId());

        return itens.stream()
                .map(carrinho -> {
                    ServicoCarrinhoDto dto = carrinhoMapper.servicoParaServicoCarrinhoDto(carrinho.getServico());
                    dto.setIdCarrinho(carrinho.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
