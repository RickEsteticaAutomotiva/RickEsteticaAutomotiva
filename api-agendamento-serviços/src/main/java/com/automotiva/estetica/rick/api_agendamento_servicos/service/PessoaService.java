// src/main/java/com/automotiva/estetica/rick/api_agendamento_servicos/service/PessoaService.java
package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.PessoaMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.LoginDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaCadastroDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaPageRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.specification.PessoaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final PessoaMapper pessoaMapper;

    public Page<PessoaDto> buscarTodosComFiltro(PessoaPageRequest pageRequest) {
        String ordenarPor = pageRequest.getOrdenarPor();
        if (ordenarPor == null || ordenarPor.isBlank()) {
            ordenarPor = "id";
        }
        String[] camposOrdenacao = ordenarPor.split(",");
        for (int i = 0; i < camposOrdenacao.length; i++) {
            camposOrdenacao[i] = camposOrdenacao[i].trim();
        }

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.getPagina(),
                pageRequest.getTamanho(),
                org.springframework.data.domain.Sort.by(camposOrdenacao)
        );

        Specification<PessoaEntity> spec = PessoaSpecification.filtroUnico(pageRequest.getFiltro());
        Page<PessoaEntity> paginaPessoas = pessoaRepository.findAll(spec, pageable);

        return paginaPessoas.map(pessoaMapper::pessoaParaPessoaDto);
    }

    public PessoaDto login(LoginDto loginDto) {
        Optional<PessoaEntity> autenticado = pessoaRepository.findByEmailAndSenha(loginDto.getEmail(), loginDto.getSenha());
        if (autenticado.isPresent()) {
            return pessoaMapper.pessoaParaPessoaDto(autenticado.get());
        }
        throw new RuntimeException("Invalid credentials");
    }

    public PessoaCadastroDto criarPessoa(PessoaCadastroDto pessoa) {
        if (pessoaRepository.existsByCpf(pessoa.getCpf())) {
            throw new RecursoJaExisteException("CPF");
        }
        if (pessoaRepository.existsByEmail(pessoa.getEmail())) {
            throw new RecursoJaExisteException("Email");
        }
        PessoaEntity pessoaEntity = pessoaMapper.pessoaCadastroDtoParaPessoaEntity(pessoa);
        pessoaEntity.setSenha(pessoa.getSenha());
        pessoaRepository.save(pessoaEntity);
        return pessoa;
    }

    public PessoaDto buscarPorId(Long id) {
        Optional<PessoaEntity> pessoa = pessoaRepository.findById(id);
        if (pessoa.isEmpty()) {
            throw new RecursoNaoEncontradaException("Pessoa");
        }
        return pessoaMapper.pessoaParaPessoaDto(pessoa.get());
    }

    public PessoaCadastroDto atualizarPessoa(Long id, PessoaCadastroDto pessoaAtualizada) {
        Optional<PessoaEntity> pessoaExistente = pessoaRepository.findById(id);
        if (pessoaExistente.isEmpty()) {
            throw new RecursoNaoEncontradaException("Pessoa");
        }
        PessoaEntity pessoa = pessoaExistente.get();
        if (!pessoa.getCpf().equals(pessoaAtualizada.getCpf()) &&
                pessoaRepository.existsByCpf(pessoaAtualizada.getCpf())) {
            throw new RecursoJaExisteException("CPF");
        }
        if (!pessoa.getEmail().equals(pessoaAtualizada.getEmail()) &&
                pessoaRepository.existsByEmail(pessoaAtualizada.getEmail())) {
            throw new RecursoJaExisteException("Email");
        }
        pessoaMapper.atualizarPessoaEntityFromDto(pessoaAtualizada, pessoa);
        pessoa.setSenha(pessoaAtualizada.getSenha());
        pessoaRepository.save(pessoa);
        return pessoaAtualizada;
    }

    public void deletarPessoa(Long id) {
        Optional<PessoaEntity> pessoa = pessoaRepository.findById(id);
        if (pessoa.isEmpty()) {
            throw new RecursoNaoEncontradaException("Pessoa");
        }
        pessoaRepository.deleteById(id);
    }
}
