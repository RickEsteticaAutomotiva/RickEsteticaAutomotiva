package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.PessoaMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.config.GerenciadorTokenJwt;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.*;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.LoginDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaCadastroDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.page_request.DefaultPageRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.specification.PessoaSpecification;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PessoaService implements UserDetailsService {
    private final PessoaRepository pessoaRepository;

    private final PessoaMapper pessoaMapper;

    private final GerenciadorTokenJwt gerenciadorTokenJwt;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    public Page<PessoaDto> buscarTodosComFiltro(DefaultPageRequest pageRequest) {
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
                org.springframework.data.domain.Sort.by(camposOrdenacao));

        Specification<PessoaEntity> spec = PessoaSpecification.filtroUnico(pageRequest.getFiltro());
        Page<PessoaEntity> paginaPessoas = pessoaRepository.findAll(spec, pageable);

        return paginaPessoas.map(pessoaMapper::pessoaParaPessoaDto);
    }

    public PessoaTokenDto login(LoginDto loginDto) {
        // 1. Cria as credenciais
        final UsernamePasswordAuthenticationToken credentials =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getSenha());

        // 2. Autentica
        final Authentication authentication = this.authenticationManager.authenticate(credentials);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Obtém o UserDetails
        PessoaDetalhesDto pessoaDetalhes = (PessoaDetalhesDto) authentication.getPrincipal();

        // 4. Busca a entidade no banco (necessário para gerar token com dados completos)
        PessoaEntity pessoa = pessoaRepository
                .findByEmail(pessoaDetalhes.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado após autenticação"));

        // 5. Gera token
        final String token = gerenciadorTokenJwt.generateToken(authentication);

        // 6. Retorna DTO com dados + token
        return pessoaMapper.PessoaParaPessoaToken(pessoa, token);
    }

    public PessoaCadastroDto criarPessoa(PessoaCadastroDto pessoa) {
        if (pessoaRepository.existsByCpf(pessoa.getCpf())) {
            throw RecursoJaExisteException.builder()
                    .mensagem("o cpf já existe no sistema")
                    .detalhes("")
                    .build();
        }
        if (pessoaRepository.existsByEmail(pessoa.getEmail())) {
            throw RecursoJaExisteException.builder()
                    .mensagem("o email já existe no sistema")
                    .detalhes("")
                    .build();
        }
        PessoaEntity pessoaEntity = pessoaMapper.pessoaCadastroDtoParaPessoaEntity(pessoa);
        String senhaCodificada = passwordEncoder.encode(pessoa.getSenha());
        pessoaEntity.setSenha(senhaCodificada);
        pessoaRepository.save(pessoaEntity);
        return pessoa;
    }

    public PessoaDto buscarPorId(Long id) {
        Optional<PessoaEntity> pessoa = pessoaRepository.findById(id);

        if (pessoa.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("a pessoa com id " + id + " não foi encontrada")
                    .detalhes("")
                    .build();
        }

        return pessoaMapper.pessoaParaPessoaDto(pessoa.get());
    }

    public PessoaAtualizadaDto atualizarPessoa(Long id, PessoaAtualizadaDto pessoaAtualizada) {
        Optional<PessoaEntity> pessoaExistente = pessoaRepository.findById(id);

        if (pessoaExistente.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("a pessoa com id " + id + " não foi encontrada")
                    .detalhes("")
                    .build();
        }

        PessoaEntity pessoa = pessoaExistente.get();
        if (!pessoa.getCpf().equals(pessoaAtualizada.getCpf())
                && pessoaRepository.existsByCpf(pessoaAtualizada.getCpf())) {
            throw RecursoJaExisteException.builder()
                    .mensagem("o cpf já existe no sistema")
                    .detalhes("")
                    .build();
        }

        if (!pessoa.getEmail().equals(pessoaAtualizada.getEmail())
                && pessoaRepository.existsByEmail(pessoaAtualizada.getEmail())) {
            throw RecursoJaExisteException.builder()
                    .mensagem("o email já existe no sistema")
                    .detalhes("")
                    .build();
        }

        pessoaMapper.atualizarPessoaEntityFromDto(pessoaAtualizada, pessoa);
        pessoaRepository.save(pessoa);
        return pessoaAtualizada;
    }

    public void deletarPessoa(Long id) {
        Optional<PessoaEntity> pessoa = pessoaRepository.findById(id);

        if (pessoa.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("a pessoa com id " + id + " não foi encontrada")
                    .detalhes("")
                    .build();
        }

        pessoaRepository.deleteById(id);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<PessoaEntity> pessoaOpt = pessoaRepository.findByEmail(username);
        if (pessoaOpt.isEmpty()) {
            throw new UsernameNotFoundException(String.format("usuario: %s não encontrado", username));
        }

        return new PessoaDetalhesDto(pessoaOpt.get());
    }

    public void atualizarSenhaPessoa(Long id, SenhaDto senhaDto) {
        String mensagemErro = "dados de senha inválidos";
        if (senhaDto == null) {
            throw CampoInvalidoException.builder()
                    .mensagem(mensagemErro)
                    .detalhes("")
                    .build();
        }

        if (senhaDto.getSenhaAtual() == null || senhaDto.getNovaSenha() == null) {
            throw CampoInvalidoException.builder()
                    .mensagem(mensagemErro)
                    .detalhes("")
                    .build();
        }

        if (senhaDto.getSenhaAtual().isBlank() || senhaDto.getNovaSenha().isBlank()) {
            throw CampoInvalidoException.builder()
                    .mensagem(mensagemErro)
                    .detalhes("")
                    .build();
        }

        Optional<PessoaEntity> pessoaOpt = pessoaRepository.findById(id);

        if (pessoaOpt.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("a pessoa com id " + id + " não foi encontrada")
                    .detalhes("")
                    .build();
        }

        PessoaEntity pessoa = pessoaOpt.get();

        if (!passwordEncoder.matches(senhaDto.getSenhaAtual(), pessoa.getSenha())) {
            throw CampoInvalidoException.builder()
                    .mensagem(mensagemErro)
                    .detalhes("")
                    .build();
        }

        pessoa.setSenha(passwordEncoder.encode(senhaDto.getNovaSenha()));

        pessoaRepository.save(pessoa);
    }
}
