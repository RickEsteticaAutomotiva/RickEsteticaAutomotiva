package com.automotiva.estetica.rick.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.assembler.PessoaTokenResponseAssembler;
import com.automotiva.estetica.rick.application.assembler.PessoaUserDetailsAssembler;
import com.automotiva.estetica.rick.application.dto.request.LoginRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaAtualizacaoRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaCadastroRequest;
import com.automotiva.estetica.rick.application.dto.request.SenhaRequest;
import com.automotiva.estetica.rick.application.dto.response.PessoaResponse;
import com.automotiva.estetica.rick.application.dto.response.TokenResponse;
import com.automotiva.estetica.rick.application.mapper.PessoaDTOMapper;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.usecase.AtualizarPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.AtualizarSenhaPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarPessoaPorIdUseCase;
import com.automotiva.estetica.rick.domain.usecase.CadastrarPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.DeletarPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarPessoasUseCase;
import com.automotiva.estetica.rick.domain.usecase.LoginPessoaUseCase;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class PessoaApplicationServiceTest {

    @Mock
    private ListarPessoasUseCase listarPessoasUseCase;
    @Mock
    private BuscarPessoaPorIdUseCase buscarPessoaPorIdUseCase;
    @Mock
    private CadastrarPessoaUseCase cadastrarPessoaUseCase;
    @Mock
    private AtualizarPessoaUseCase atualizarPessoaUseCase;
    @Mock
    private DeletarPessoaUseCase deletarPessoaUseCase;
    @Mock
    private LoginPessoaUseCase loginPessoaUseCase;
    @Mock
    private AtualizarSenhaPessoaUseCase atualizarSenhaPessoaUseCase;
    @Mock
    private PessoaDTOMapper pessoaDTOMapper;
    @Mock
    private JwtService jwtService;
    @Mock
    private PessoaTokenResponseAssembler pessoaTokenResponseAssembler;
    @Mock
    private PessoaUserDetailsAssembler pessoaUserDetailsAssembler;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private PessoaApplicationService pessoaApplicationService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private Pessoa pessoaMock() {
        return Pessoa.builder().id(1L).nome("Joao Silva").cpf("123.456.789-00").email("joao@email.com")
                .telefone("11999999999").dataNascimento(LocalDate.of(1990, 1, 15)).senha("$2a$10$hash")
                .roles(EnumSet.of(RoleEnum.ROLE_CLIENTE)).build();
    }

    @Test
    @DisplayName("buscarTodos - delega ao use case e mapeia pagina")
    void buscarTodos_sucesso() {
        Pessoa pessoa = pessoaMock();
        PessoaResponse response = PessoaResponse.builder().id(1L).email("joao@email.com").build();
        Page<Pessoa> page = new PageImpl<>(List.of(pessoa));

        when(listarPessoasUseCase.execute(eq("joao"), any())).thenReturn(page);
        when(pessoaDTOMapper.toResponse(pessoa)).thenReturn(response);

        PageRequest request = new PageRequest();
        request.setPagina(0);
        request.setTamanho(10);
        request.setFiltro("joao");

        Page<PessoaResponse> resultado = pessoaApplicationService.buscarTodos(request);

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().getFirst().getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("buscarPorId - delega ao use case e mapeia DTO")
    void buscarPorId_sucesso() {
        Pessoa pessoa = pessoaMock();
        PessoaResponse response = PessoaResponse.builder().id(1L).nome("Joao Silva").build();

        when(buscarPessoaPorIdUseCase.execute(1L)).thenReturn(pessoa);
        when(pessoaDTOMapper.toResponse(pessoa)).thenReturn(response);

        PessoaResponse resultado = pessoaApplicationService.buscarPorId(1L);

        assertThat(resultado.getNome()).isEqualTo("Joao Silva");
    }

    @Test
    @DisplayName("buscarPorId - propaga RecursoNaoEncontradoException")
    void buscarPorId_naoEncontrado() {
        when(buscarPessoaPorIdUseCase.execute(99L)).thenThrow(RecursoNaoEncontradoException.builder()
                .mensagem("a pessoa com id 99 nao foi encontrada").detalhes("").build());

        assertThrows(RecursoNaoEncontradoException.class, () -> pessoaApplicationService.buscarPorId(99L));
    }

    @Test
    @DisplayName("cadastrar - converte request, delega use case e mapeia retorno")
    void cadastrar_sucesso() {
        PessoaCadastroRequest request = new PessoaCadastroRequest();
        request.setNome("Joao Silva");
        request.setEmail("joao@email.com");
        request.setSenha("Teste@1234");
        request.setRoles(EnumSet.of(RoleEnum.ROLE_CLIENTE));

        Pessoa entrada = Pessoa.builder().nome("Joao Silva").email("joao@email.com").senha("Teste@1234").build();
        Pessoa criada = pessoaMock();
        PessoaResponse response = PessoaResponse.builder().id(1L).email("joao@email.com").build();

        when(pessoaDTOMapper.toDomain(request)).thenReturn(entrada);
        when(cadastrarPessoaUseCase.execute(entrada, request.getRoles())).thenReturn(criada);
        when(pessoaDTOMapper.toResponse(criada)).thenReturn(response);

        PessoaResponse resultado = pessoaApplicationService.cadastrar(request);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(cadastrarPessoaUseCase).execute(entrada, request.getRoles());
    }

    @Test
    @DisplayName("atualizar - delega parametros ao use case e mapeia DTO")
    void atualizar_sucesso() {
        PessoaAtualizacaoRequest request = new PessoaAtualizacaoRequest();
        request.setNome("Joao Atualizado");
        request.setCpf("123.456.789-00");
        request.setEmail("joao@email.com");

        Pessoa atualizada = pessoaMock();
        atualizada.setNome("Joao Atualizado");
        PessoaResponse response = PessoaResponse.builder().id(1L).nome("Joao Atualizado").build();

        when(atualizarPessoaUseCase.execute(1L, request.getNome(), request.getCpf(), request.getEmail(),
                request.getTelefone(), request.getDataNascimento())).thenReturn(atualizada);
        when(pessoaDTOMapper.toResponse(atualizada)).thenReturn(response);

        PessoaResponse resultado = pessoaApplicationService.atualizar(1L, request);

        assertThat(resultado.getNome()).isEqualTo("Joao Atualizado");
    }

    @Test
    @DisplayName("deletar - delega ao use case")
    void deletar_sucesso() {
        pessoaApplicationService.deletar(7L);

        verify(deletarPessoaUseCase).execute(7L);
    }

    @Test
    @DisplayName("login - autentica, gera token e retorna payload")
    void login_sucesso() {
        LoginRequest request = new LoginRequest();
        request.setEmail("joao@email.com");
        request.setSenha("Teste@1234");

        Pessoa pessoa = pessoaMock();
        Authentication auth = new UsernamePasswordAuthenticationToken("joao@email.com", "Teste@1234");
        TokenResponse tokenResponse = TokenResponse.builder().id(1L).email("joao@email.com").nome("Joao Silva")
                .token("jwt.token").roles(EnumSet.of(RoleEnum.ROLE_CLIENTE)).build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(loginPessoaUseCase.execute("joao@email.com")).thenReturn(pessoa);
        when(jwtService.gerarToken(auth)).thenReturn("jwt.token");
        when(pessoaTokenResponseAssembler.toTokenResponse(pessoa, "jwt.token")).thenReturn(tokenResponse);

        TokenResponse resultado = pessoaApplicationService.login(request);

        assertThat(resultado.getToken()).isEqualTo("jwt.token");
        assertThat(resultado.getRoles()).contains(RoleEnum.ROLE_CLIENTE);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(auth);
        verify(pessoaTokenResponseAssembler).toTokenResponse(pessoa, "jwt.token");
    }

    @Test
    @DisplayName("login - traduz falha de autenticacao para BadCredentialsException")
    void login_credenciaisInvalidas() {
        LoginRequest request = new LoginRequest();
        request.setEmail("joao@email.com");
        request.setSenha("senhaErrada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("credenciais invalidas"));

        assertThrows(BadCredentialsException.class, () -> pessoaApplicationService.login(request));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(loginPessoaUseCase, never()).execute(any());
        verify(jwtService, never()).gerarToken(any());
        verify(pessoaTokenResponseAssembler, never()).toTokenResponse(any(), any());
    }

    @Test
    @DisplayName("atualizarSenha - delega ao use case")
    void atualizarSenha_sucesso() {
        SenhaRequest request = new SenhaRequest();
        request.setSenhaAtual("atual");
        request.setNovaSenha("nova123");

        pessoaApplicationService.atualizarSenha(1L, request);

        verify(atualizarSenhaPessoaUseCase).execute(1L, "atual", "nova123");
    }

    @Test
    @DisplayName("loadUserByUsername - aplica fallback ROLE_CLIENTE quando roles vazio")
    void loadUserByUsername_rolesVazio() {
        Pessoa semRole = Pessoa.builder().id(5L).email("semrole@email.com").senha("$2a$10$hash")
                .roles(EnumSet.noneOf(RoleEnum.class)).build();
        User expectedUserDetails = new User("semrole@email.com", "$2a$10$hash",
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CLIENTE")));

        when(loginPessoaUseCase.execute("semrole@email.com")).thenReturn(semRole);
        when(pessoaUserDetailsAssembler.toUserDetails(semRole)).thenReturn(expectedUserDetails);

        var userDetails = pessoaApplicationService.loadUserByUsername("semrole@email.com");

        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_CLIENTE");
        verify(pessoaUserDetailsAssembler).toUserDetails(semRole);
    }

    @Test
    @DisplayName("loadUserByUsername - propaga UsernameNotFoundException quando usuário não existe")
    void loadUserByUsername_usuarioNaoEncontrado() {
        when(loginPessoaUseCase.execute("missing@email.com"))
                .thenThrow(new UsernameNotFoundException("Usuário não encontrado: missing@email.com"));

        assertThrows(UsernameNotFoundException.class,
                () -> pessoaApplicationService.loadUserByUsername("missing@email.com"));
        verify(pessoaUserDetailsAssembler, never()).toUserDetails(any());
    }
}
