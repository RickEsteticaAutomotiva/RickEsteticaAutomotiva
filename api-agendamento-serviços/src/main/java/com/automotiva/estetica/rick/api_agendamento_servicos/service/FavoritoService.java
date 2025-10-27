package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.FavoritoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.ServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.FavoritoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoFavoritoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.FavoritoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.FavoritoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritoService {
    private final FavoritoRepository favoritoRepository;
    private final PessoaRepository pessoaRepository;
    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;
    private final FavoritoMapper favoritoMapper;


    public void adicionarFavorito(FavoritoDto favoritoDto) {
        PessoaEntity pessoa = pessoaRepository.findById(favoritoDto.getIdPessoa())
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Usuário não encontrado: " + favoritoDto.getIdPessoa())
                        .detalhes("")
                        .build());
        ServicoEntity servico = servicoRepository.findById(favoritoDto.getIdServico())
                .orElseThrow(() -> RecursoNaoEncontradaException.builder()
                        .mensagem("Serviço não encontrado: " + favoritoDto.getIdServico())
                        .detalhes("")
                        .build());

        if (favoritoRepository.existsByPessoaAndServico(pessoa, servico)) {
            throw RecursoJaExisteException.builder()
                    .mensagem("Esse servico já existe para este usuário.")
                    .detalhes("")
                    .build();
        }

        FavoritoEntity favoritoEntity = favoritoMapper.favoritoDtoParaEntity(favoritoDto);

        favoritoRepository.save(favoritoEntity);
    }

    public void removerFavorito(Long idFavorito) {
        favoritoRepository.findById(idFavorito)
                .orElseThrow(() -> RecursoJaExisteException.builder()
                    .mensagem("Favorito não encontrado para este usuário.")
                    .detalhes("")
                    .build());

        favoritoRepository.deleteById(idFavorito);
    }

    public List<ServicoFavoritoDto> listarFavoritosPessoa(Long idPessoa) {
        if (!pessoaRepository.existsById(idPessoa)) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("Usuário não encontrado: " + idPessoa)
                    .detalhes("")
                    .build();
        }

        List<FavoritoEntity> itens = favoritoRepository.findByPessoaId(idPessoa);

        return itens.stream()
                .map(favorito -> {
                    ServicoFavoritoDto dto = favoritoMapper.servicoParaServicoFavoritoDto(favorito.getServico());
                    dto.setIdFavorito(favorito.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
