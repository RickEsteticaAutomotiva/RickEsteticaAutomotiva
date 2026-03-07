package com.automotiva.estetica.rick.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.dto.request.LoginRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaAtualizacaoRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaCadastroRequest;
import com.automotiva.estetica.rick.application.dto.request.SenhaRequest;
import com.automotiva.estetica.rick.application.dto.response.PessoaResponse;
import com.automotiva.estetica.rick.application.dto.response.TokenResponse;
import com.automotiva.estetica.rick.application.port.out.PessoaRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock private PessoaRepositoryPort pessoaRepositoryPort;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private JwtService jwtService;

    @Mock private AuthenticationManager authenticationManager;

    private PessoaService pessoaService;

    @BeforeEach
    void setUp() {
        // Instanciação manual para injetar @Lazy AuthenticationManager sem contexto Spring
        pessoaService =
                new PessoaService(
                        pessoaRepositoryPort, passwordEncoder, jwtService, authenticationManager);
    }

    private Pessoa pessoaMock() {
        return Pessoa.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("123.456.789-00")
                .email("joao@email.com")
                .telefone("11999999999")
                .dataNascimento(LocalDate.of(1990, 1, 15))
                .senha("$2a$10$encodedPassword")
                .roles(EnumSet.of(RoleEnum.ROLE_CLIENTE))
                .build();
    }

    private Pessoa pessoaAdminMock() {
        return Pessoa.builder()
                .id(2L)
                .nome("Admin User")
                .cpf("000.000.000-01")
                .email("admin@email.com")
                .telefone("11999990000")
                .dataNascimento(LocalDate.of(1985, 3, 10))
                .senha("$2a$10$encodedPassword")
                .roles(EnumSet.of(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_CLIENTE))
                .build();
    }

    private PessoaCadastroRequest cadastroRequestMock() {
        PessoaCadastroRequest req = new PessoaCadastroRequest();
        req.setNome("João Silva");
        req.setCpf("123.456.789-00");
        req.setEmail("joao@email.com");
        req.setTelefone("11999999999");
        req.setDataNascimento(LocalDate.of(1990, 1, 15));
        req.setSenha("senha123");
        return req;
    }

    // ─── buscarTodos ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar página de pessoas com sucesso")
    void buscarTodos_sucesso() {
        Page<Pessoa> page = new PageImpl<>(List.of(pessoaMock()));
        when(pessoaRepositoryPort.buscarTodos(isNull(), any(Pageable.class))).thenReturn(page);

        PageRequest req = new PageRequest();
        req.setPagina(0);
        req.setTamanho(10);

        Page<PessoaResponse> resultado = pessoaService.buscarTodos(req);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("João Silva", resultado.getContent().getFirst().getNome());
    }

    // ─── buscarPorId ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao buscar pessoa por ID inexistente")
    void buscarPorId_inexistente_deveLancarExcecao() {
        when(pessoaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> pessoaService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve retornar pessoa por ID com sucesso")
    void buscarPorId_sucesso() {
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoaMock()));

        PessoaResponse resultado = pessoaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("joao@email.com", resultado.getEmail());
    }

    // ─── cadastrar ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar quando CPF já existir")
    void cadastrar_cpfDuplicado_deveLancarExcecao() {
        when(pessoaRepositoryPort.existePorCpf("123.456.789-00")).thenReturn(true);

        assertThrows(
                RecursoJaExisteException.class,
                () -> pessoaService.cadastrar(cadastroRequestMock()));
        verify(pessoaRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar quando e-mail já existir")
    void cadastrar_emailDuplicado_deveLancarExcecao() {
        when(pessoaRepositoryPort.existePorCpf("123.456.789-00")).thenReturn(false);
        when(pessoaRepositoryPort.existePorEmail("joao@email.com")).thenReturn(true);

        assertThrows(
                RecursoJaExisteException.class,
                () -> pessoaService.cadastrar(cadastroRequestMock()));
        verify(pessoaRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve cadastrar pessoa com sucesso")
    void cadastrar_sucesso() {
        Pessoa salva = pessoaMock();
        when(pessoaRepositoryPort.existePorCpf("123.456.789-00")).thenReturn(false);
        when(pessoaRepositoryPort.existePorEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$encodedPassword");
        when(pessoaRepositoryPort.salvar(any(Pessoa.class))).thenReturn(salva);

        PessoaResponse resultado = pessoaService.cadastrar(cadastroRequestMock());

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        verify(pessoaRepositoryPort).salvar(any(Pessoa.class));
    }

    @Test
    @DisplayName("Deve cadastrar com ROLE_CLIENTE por padrão quando roles não informadas")
    void cadastrar_semRoles_deveAtribuirRoleUserPorPadrao() {
        Pessoa salva = pessoaMock(); // roles = ROLE_CLIENTE
        when(pessoaRepositoryPort.existePorCpf(any())).thenReturn(false);
        when(pessoaRepositoryPort.existePorEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$encodedPassword");
        when(pessoaRepositoryPort.salvar(any(Pessoa.class))).thenReturn(salva);

        PessoaResponse resultado = pessoaService.cadastrar(cadastroRequestMock()); // roles == null

        assertNotNull(resultado.getRoles());
        assertTrue(resultado.getRoles().contains(RoleEnum.ROLE_CLIENTE));
    }

    @Test
    @DisplayName("Deve cadastrar com múltiplas roles quando informadas no request")
    void cadastrar_comMultiplasRoles_devePersistirTodas() {
        Pessoa salva =
                Pessoa.builder()
                        .id(3L)
                        .nome("Admin Gerente")
                        .cpf("111.222.333-44")
                        .email("ag@email.com")
                        .senha("$2a$10$encodedPassword")
                        .roles(EnumSet.of(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_GERENTE))
                        .build();

        when(pessoaRepositoryPort.existePorCpf(any())).thenReturn(false);
        when(pessoaRepositoryPort.existePorEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$encodedPassword");
        when(pessoaRepositoryPort.salvar(any(Pessoa.class))).thenReturn(salva);

        PessoaCadastroRequest req = cadastroRequestMock();
        req.setRoles(Set.of(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_GERENTE));

        PessoaResponse resultado = pessoaService.cadastrar(req);

        assertEquals(2, resultado.getRoles().size());
        assertTrue(resultado.getRoles().contains(RoleEnum.ROLE_ADMIN));
        assertTrue(resultado.getRoles().contains(RoleEnum.ROLE_GERENTE));
    }

    // ─── atualizar ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao atualizar pessoa inexistente")
    void atualizar_pessoaNaoEncontrada_deveLancarExcecao() {
        when(pessoaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        PessoaAtualizacaoRequest req = new PessoaAtualizacaoRequest();
        assertThrows(RecursoNaoEncontradoException.class, () -> pessoaService.atualizar(99L, req));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar quando novo CPF já pertencer a outra pessoa")
    void atualizar_cpfDuplicado_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(pessoaRepositoryPort.existePorCpf("999.888.777-66")).thenReturn(true);

        PessoaAtualizacaoRequest req = new PessoaAtualizacaoRequest();
        req.setCpf("999.888.777-66");

        assertThrows(RecursoJaExisteException.class, () -> pessoaService.atualizar(1L, req));
        verify(pessoaRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar quando novo e-mail já pertencer a outra pessoa")
    void atualizar_emailDuplicado_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(pessoaRepositoryPort.existePorEmail("outro@email.com")).thenReturn(true);

        PessoaAtualizacaoRequest req = new PessoaAtualizacaoRequest();
        req.setEmail("outro@email.com");

        assertThrows(RecursoJaExisteException.class, () -> pessoaService.atualizar(1L, req));
        verify(pessoaRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve atualizar pessoa com sucesso")
    void atualizar_sucesso() {
        Pessoa pessoa = pessoaMock();
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(pessoaRepositoryPort.salvar(pessoa)).thenReturn(pessoa);

        PessoaAtualizacaoRequest req = new PessoaAtualizacaoRequest();
        req.setNome("João Atualizado");

        PessoaResponse resultado = pessoaService.atualizar(1L, req);

        assertNotNull(resultado);
        assertEquals("João Atualizado", pessoa.getNome());
        verify(pessoaRepositoryPort).salvar(pessoa);
    }

    // ─── deletar ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao deletar pessoa inexistente")
    void deletar_inexistente_deveLancarExcecao() {
        when(pessoaRepositoryPort.existePorId(99L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> pessoaService.deletar(99L));
        verify(pessoaRepositoryPort, never()).deletarPorId(any());
    }

    @Test
    @DisplayName("Deve deletar pessoa com sucesso")
    void deletar_sucesso() {
        when(pessoaRepositoryPort.existePorId(1L)).thenReturn(true);

        assertDoesNotThrow(() -> pessoaService.deletar(1L));
        verify(pessoaRepositoryPort).deletarPorId(1L);
    }

    // ─── atualizarSenha ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção ao atualizar senha de pessoa inexistente")
    void atualizarSenha_pessoaNaoEncontrada_deveLancarExcecao() {
        when(pessoaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        SenhaRequest req = new SenhaRequest();
        req.setSenhaAtual("atual");
        req.setNovaSenha("nova");

        assertThrows(
                RecursoNaoEncontradoException.class, () -> pessoaService.atualizarSenha(99L, req));
    }

    @Test
    @DisplayName("Deve lançar CampoInvalidoException quando senhaAtual for nula")
    void atualizarSenha_senhaAtualNula_deveLancarExcecao() {
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoaMock()));

        SenhaRequest req = new SenhaRequest();
        req.setSenhaAtual(null);
        req.setNovaSenha("nova123");

        assertThrows(CampoInvalidoException.class, () -> pessoaService.atualizarSenha(1L, req));
    }

    @Test
    @DisplayName("Deve lançar CampoInvalidoException quando senha atual não conferir")
    void atualizarSenha_senhaAtualInvalida_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(passwordEncoder.matches("senhaErrada", pessoa.getSenha())).thenReturn(false);

        SenhaRequest req = new SenhaRequest();
        req.setSenhaAtual("senhaErrada");
        req.setNovaSenha("nova123");

        assertThrows(CampoInvalidoException.class, () -> pessoaService.atualizarSenha(1L, req));
        verify(pessoaRepositoryPort, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve atualizar senha com sucesso")
    void atualizarSenha_sucesso() {
        Pessoa pessoa = pessoaMock();
        when(pessoaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(passwordEncoder.matches("senhaAtual", pessoa.getSenha())).thenReturn(true);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("$2a$10$newEncoded");

        SenhaRequest req = new SenhaRequest();
        req.setSenhaAtual("senhaAtual");
        req.setNovaSenha("novaSenha123");

        assertDoesNotThrow(() -> pessoaService.atualizarSenha(1L, req));
        assertEquals("$2a$10$newEncoded", pessoa.getSenha());
        verify(pessoaRepositoryPort).salvar(pessoa);
    }

    // ─── login ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar token")
    void login_sucesso() {
        Pessoa pessoa = pessoaMock();
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(pessoaRepositoryPort.buscarPorEmail("joao@email.com")).thenReturn(Optional.of(pessoa));
        when(jwtService.gerarToken(auth)).thenReturn("jwt.token.aqui");

        LoginRequest req = new LoginRequest();
        req.setEmail("joao@email.com");
        req.setSenha("senha123");

        TokenResponse resultado = pessoaService.login(req);

        assertNotNull(resultado);
        assertEquals("jwt.token.aqui", resultado.getToken());
        assertEquals("joao@email.com", resultado.getEmail());
        assertEquals(1L, resultado.getId());
        assertNotNull(resultado.getRoles());
        assertTrue(resultado.getRoles().contains(RoleEnum.ROLE_CLIENTE));
    }

    @Test
    @DisplayName("Deve retornar múltiplas roles no TokenResponse para usuário ADMIN+CLIENTE")
    void login_adminComMultiplasRoles_deveRetornarTodasNoToken() {
        Pessoa admin = pessoaAdminMock();
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(pessoaRepositoryPort.buscarPorEmail("admin@email.com")).thenReturn(Optional.of(admin));
        when(jwtService.gerarToken(auth)).thenReturn("jwt.admin.token");

        LoginRequest req = new LoginRequest();
        req.setEmail("admin@email.com");
        req.setSenha("senha123");

        TokenResponse resultado = pessoaService.login(req);

        assertEquals(2, resultado.getRoles().size());
        assertTrue(resultado.getRoles().contains(RoleEnum.ROLE_ADMIN));
        assertTrue(resultado.getRoles().contains(RoleEnum.ROLE_CLIENTE));
    }

    // ─── loadUserByUsername ─────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando e-mail não existir")
    void loadUserByUsername_emailNaoEncontrado_deveLancarExcecao() {
        when(pessoaRepositoryPort.buscarPorEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> pessoaService.loadUserByUsername("inexistente@email.com"));
    }

    @Test
    @DisplayName("Deve carregar UserDetails com sucesso e uma role")
    void loadUserByUsername_sucesso() {
        when(pessoaRepositoryPort.buscarPorEmail("joao@email.com"))
                .thenReturn(Optional.of(pessoaMock()));

        var userDetails = pessoaService.loadUserByUsername("joao@email.com");

        assertNotNull(userDetails);
        assertEquals("joao@email.com", userDetails.getUsername());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE")));
    }

    @Test
    @DisplayName("Deve carregar UserDetails com múltiplas GrantedAuthorities para ADMIN+CLIENTE")
    void loadUserByUsername_multiRole_deveGerarMultiplasAuthorities() {
        when(pessoaRepositoryPort.buscarPorEmail("admin@email.com"))
                .thenReturn(Optional.of(pessoaAdminMock()));

        var userDetails = pessoaService.loadUserByUsername("admin@email.com");

        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE")));
    }

    @Test
    @DisplayName("Deve aplicar ROLE_CLIENTE como fallback quando roles da pessoa estiver vazio")
    void loadUserByUsername_roleVazia_deveFallbackParaRoleUser() {
        Pessoa semRole =
                Pessoa.builder()
                        .id(5L)
                        .nome("Sem Role")
                        .email("semrole@email.com")
                        .senha("$2a$10$hash")
                        .roles(EnumSet.noneOf(RoleEnum.class)) // set vazio
                        .build();

        when(pessoaRepositoryPort.buscarPorEmail("semrole@email.com"))
                .thenReturn(Optional.of(semRole));

        var userDetails = pessoaService.loadUserByUsername("semrole@email.com");

        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_CLIENTE", userDetails.getAuthorities().iterator().next().getAuthority());
    }
}
