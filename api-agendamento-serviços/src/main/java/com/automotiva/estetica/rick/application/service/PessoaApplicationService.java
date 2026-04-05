package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.assembler.PessoaTokenResponseAssembler;
import com.automotiva.estetica.rick.application.assembler.PessoaUserDetailsAssembler;
import com.automotiva.estetica.rick.application.PageableFactory;
import com.automotiva.estetica.rick.application.dto.request.LoginRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaAtualizacaoRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaCadastroRequest;
import com.automotiva.estetica.rick.application.dto.request.SenhaRequest;
import com.automotiva.estetica.rick.application.dto.response.PessoaResponse;
import com.automotiva.estetica.rick.application.dto.response.TokenResponse;
import com.automotiva.estetica.rick.application.mapper.PessoaDTOMapper;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.usecase.AtualizarPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.AtualizarSenhaPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarPessoaPorIdUseCase;
import com.automotiva.estetica.rick.domain.usecase.CadastrarPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.DeletarPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarPessoasUseCase;
import com.automotiva.estetica.rick.domain.usecase.LoginPessoaUseCase;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PessoaApplicationService implements UserDetailsService {

    private final ListarPessoasUseCase listarPessoasUseCase;
    private final BuscarPessoaPorIdUseCase buscarPessoaPorIdUseCase;
    private final CadastrarPessoaUseCase cadastrarPessoaUseCase;
    private final AtualizarPessoaUseCase atualizarPessoaUseCase;
    private final DeletarPessoaUseCase deletarPessoaUseCase;
    private final LoginPessoaUseCase loginPessoaUseCase;
    private final AtualizarSenhaPessoaUseCase atualizarSenhaPessoaUseCase;
    private final PessoaDTOMapper pessoaDTOMapper;
    private final JwtService jwtService;
    private final PessoaTokenResponseAssembler pessoaTokenResponseAssembler;
    private final PessoaUserDetailsAssembler pessoaUserDetailsAssembler;
    private final AuthenticationManager authenticationManager;

    public PessoaApplicationService(ListarPessoasUseCase listarPessoasUseCase,
            BuscarPessoaPorIdUseCase buscarPessoaPorIdUseCase, CadastrarPessoaUseCase cadastrarPessoaUseCase,
            AtualizarPessoaUseCase atualizarPessoaUseCase, DeletarPessoaUseCase deletarPessoaUseCase,
            LoginPessoaUseCase loginPessoaUseCase, AtualizarSenhaPessoaUseCase atualizarSenhaPessoaUseCase,
            PessoaDTOMapper pessoaDTOMapper, JwtService jwtService,
            PessoaTokenResponseAssembler pessoaTokenResponseAssembler,
            PessoaUserDetailsAssembler pessoaUserDetailsAssembler, @Lazy AuthenticationManager authenticationManager) {
        this.listarPessoasUseCase = listarPessoasUseCase;
        this.buscarPessoaPorIdUseCase = buscarPessoaPorIdUseCase;
        this.cadastrarPessoaUseCase = cadastrarPessoaUseCase;
        this.atualizarPessoaUseCase = atualizarPessoaUseCase;
        this.deletarPessoaUseCase = deletarPessoaUseCase;
        this.loginPessoaUseCase = loginPessoaUseCase;
        this.atualizarSenhaPessoaUseCase = atualizarSenhaPessoaUseCase;
        this.pessoaDTOMapper = pessoaDTOMapper;
        this.jwtService = jwtService;
        this.pessoaTokenResponseAssembler = pessoaTokenResponseAssembler;
        this.pessoaUserDetailsAssembler = pessoaUserDetailsAssembler;
        this.authenticationManager = authenticationManager;
    }

    public Page<PessoaResponse> buscarTodos(PageRequest pageRequest) {
        Pageable pageable = PageableFactory.from(pageRequest);
        return listarPessoasUseCase.execute(pageRequest.getFiltro(), pageable).map(pessoaDTOMapper::toResponse);
    }

    public PessoaResponse buscarPorId(Long id) {
        Pessoa pessoa = buscarPessoaPorIdUseCase.execute(id);
        return pessoaDTOMapper.toResponse(pessoa);
    }

    public PessoaResponse cadastrar(PessoaCadastroRequest request) {
        Pessoa pessoa = pessoaDTOMapper.toDomain(request);
        Pessoa pessoaCriada = cadastrarPessoaUseCase.execute(pessoa, request.getRoles());
        return pessoaDTOMapper.toResponse(pessoaCriada);
    }

    public PessoaResponse atualizar(Long id, PessoaAtualizacaoRequest request) {
        Pessoa pessoa = atualizarPessoaUseCase.execute(id, request.getNome(), request.getCpf(), request.getEmail(),
                request.getTelefone(), request.getDataNascimento());
        return pessoaDTOMapper.toResponse(pessoa);
    }

    public void deletar(Long id) {
        deletarPessoaUseCase.execute(id);
    }

    public TokenResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(request.getEmail(),
                request.getSenha());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(credentials);
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new org.springframework.security.authentication.BadCredentialsException("E-mail ou senha incorretos",
                    ex);
        }

        Pessoa pessoa = loginPessoaUseCase.execute(request.getEmail());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtService.gerarToken(authentication);

        return pessoaTokenResponseAssembler.toTokenResponse(pessoa, token);
    }

    public void atualizarSenha(Long id, SenhaRequest request) {
        atualizarSenhaPessoaUseCase.execute(id, request.getSenhaAtual(), request.getNovaSenha());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Pessoa pessoa = loginPessoaUseCase.execute(username);
        return pessoaUserDetailsAssembler.toUserDetails(pessoa);
    }
}
