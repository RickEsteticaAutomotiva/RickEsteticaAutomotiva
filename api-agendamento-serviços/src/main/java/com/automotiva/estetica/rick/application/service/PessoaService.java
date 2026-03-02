package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.PageableFactory;
import com.automotiva.estetica.rick.application.dto.request.LoginRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaAtualizacaoRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaCadastroRequest;
import com.automotiva.estetica.rick.application.dto.request.SenhaRequest;
import com.automotiva.estetica.rick.application.dto.response.PessoaResponse;
import com.automotiva.estetica.rick.application.dto.response.TokenResponse;
import com.automotiva.estetica.rick.application.port.in.PessoaUseCase;
import com.automotiva.estetica.rick.application.port.out.PessoaRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PessoaService implements PessoaUseCase, UserDetailsService {

    private final PessoaRepositoryPort pessoaRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * {@code @Lazy} no AuthenticationManager quebra o ciclo circular:
     * PessoaService → AuthenticationManager (SecurityConfig)
     *               → JwtAuthFilter({@code @Lazy} UserDetailsService=PessoaService)
     */
    public PessoaService(
            PessoaRepositoryPort pessoaRepositoryPort,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Lazy AuthenticationManager authenticationManager) {
        this.pessoaRepositoryPort = pessoaRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Page<PessoaResponse> buscarTodos(PageRequest pageRequest) {
        Pageable pageable = PageableFactory.from(pageRequest);
        return pessoaRepositoryPort
                .buscarTodos(pageRequest.getFiltro(), pageable)
                .map(this::toResponse);
    }

    @Override
    public PessoaResponse buscarPorId(Long id) {
        return pessoaRepositoryPort
                .buscarPorId(id)
                .map(this::toResponse)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("a pessoa com id " + id + " não foi encontrada")
                        .detalhes("")
                        .build());
    }

    @Override
    public PessoaResponse cadastrar(PessoaCadastroRequest request) {
        if (pessoaRepositoryPort.existePorCpf(request.getCpf())) {
            throw RecursoJaExisteException.builder()
                    .mensagem("o cpf já existe no sistema")
                    .detalhes("")
                    .build();
        }
        if (pessoaRepositoryPort.existePorEmail(request.getEmail())) {
            throw RecursoJaExisteException.builder()
                    .mensagem("o email já existe no sistema")
                    .detalhes("")
                    .build();
        }

        // Roles: usa as informadas no request ou ROLE_USER por padrão (auto-cadastro público)
        Set<RoleEnum> roles =
                (request.getRoles() != null && !request.getRoles().isEmpty())
                        ? EnumSet.copyOf(request.getRoles())
                        : EnumSet.of(RoleEnum.ROLE_CLIENTE);

        Pessoa pessoa = Pessoa.builder()
                .nome(request.getNome())
                .cpf(request.getCpf())
                .email(request.getEmail())
                .telefone(request.getTelefone())
                .dataNascimento(request.getDataNascimento())
                .senha(passwordEncoder.encode(request.getSenha()))
                .roles(roles)
                .build();
        return toResponse(pessoaRepositoryPort.salvar(pessoa));
    }

    @Override
    public PessoaResponse atualizar(Long id, PessoaAtualizacaoRequest request) {
        Pessoa pessoa = pessoaRepositoryPort.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("a pessoa com id " + id + " não foi encontrada")
                .detalhes("")
                .build());

        if (request.getCpf() != null
                && !request.getCpf().equals(pessoa.getCpf())
                && pessoaRepositoryPort.existePorCpf(request.getCpf())) {
            throw RecursoJaExisteException.builder()
                    .mensagem("o cpf já existe no sistema")
                    .detalhes("")
                    .build();
        }
        if (request.getEmail() != null
                && !request.getEmail().equals(pessoa.getEmail())
                && pessoaRepositoryPort.existePorEmail(request.getEmail())) {
            throw RecursoJaExisteException.builder()
                    .mensagem("o email já existe no sistema")
                    .detalhes("")
                    .build();
        }

        pessoa.atualizar(
                request.getNome(),
                request.getCpf(),
                request.getEmail(),
                request.getTelefone(),
                request.getDataNascimento());

        return toResponse(pessoaRepositoryPort.salvar(pessoa));
    }

    @Override
    public void deletar(Long id) {
        if (!pessoaRepositoryPort.existePorId(id)) {
            throw RecursoNaoEncontradoException.builder()
                    .mensagem("a pessoa com id " + id + " não foi encontrada")
                    .detalhes("")
                    .build();
        }
        pessoaRepositoryPort.deletarPorId(id);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken credentials =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(credentials);
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("E-mail ou senha incorretos", ex);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Pessoa pessoa = pessoaRepositoryPort
                .buscarPorEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado após autenticação"));

        String token = jwtService.gerarToken(authentication);
        return TokenResponse.builder()
                .id(pessoa.getId())
                .email(pessoa.getEmail())
                .nome(pessoa.getNome())
                .token(token)
                .roles(pessoa.getRoles())
                .build();
    }

    @Override
    public void atualizarSenha(Long id, SenhaRequest request) {
        Pessoa pessoa = pessoaRepositoryPort.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("a pessoa com id " + id + " não foi encontrada")
                .detalhes("")
                .build());

        // Validação de campos nulos/em branco é regra de domínio; lança CampoInvalidoException se inválido
        String senhaAtual = request != null ? request.getSenhaAtual() : null;
        String novaSenha = request != null ? request.getNovaSenha() : null;
        pessoa.validarDadosSenha(senhaAtual, novaSenha);

        if (!passwordEncoder.matches(senhaAtual, pessoa.getSenha())) {
            throw CampoInvalidoException.builder()
                    .mensagem("dados de senha inválidos")
                    .detalhes("")
                    .build();
        }
        // Encode feito no service (infraestrutura); domínio recebe apenas a string já encodada
        pessoa.alterarSenha(passwordEncoder.encode(novaSenha));
        pessoaRepositoryPort.salvar(pessoa);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Pessoa pessoa = pessoaRepositoryPort
                .buscarPorEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Garante ao menos ROLE_USER quando roles estiver vazio/nulo
        Set<RoleEnum> roles =
                (pessoa.getRoles() != null && !pessoa.getRoles().isEmpty())
                        ? pessoa.getRoles()
                        : EnumSet.of(RoleEnum.ROLE_CLIENTE);

        // Cada role do Set vira uma GrantedAuthority — Spring Security avalia TODAS
        var authorities = roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.authority()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                pessoa.getEmail(), pessoa.getSenha(), authorities);
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private PessoaResponse toResponse(Pessoa p) {
        return PessoaResponse.builder()
                .id(p.getId())
                .nome(p.getNome())
                .cpf(p.getCpf())
                .email(p.getEmail())
                .telefone(p.getTelefone())
                .dataNascimento(p.getDataNascimento())
                .roles(p.getRoles())
                .build();
    }
}
