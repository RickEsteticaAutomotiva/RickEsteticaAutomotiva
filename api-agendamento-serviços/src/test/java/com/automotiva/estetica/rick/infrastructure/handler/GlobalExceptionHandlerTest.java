package com.automotiva.estetica.rick.infrastructure.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.port.in.ErroLogUseCase;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        ErroLogUseCase erroLogUseCase = mock(ErroLogUseCase.class);
        doNothing().when(erroLogUseCase).registrar(any());

        handler = new GlobalExceptionHandler(erroLogUseCase);

        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        when(request.getHeader("User-Agent")).thenReturn("JUnit");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);

        // Popula SecurityContext com usuário autenticado (simula ROLE_ADMIN autenticado)
        // necessário para que handleAccessDenied retorne 403 (não 401 para anônimos)
        var auth = new UsernamePasswordAuthenticationToken(
                "admin@test.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─── DomainException ────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar 404 para RecursoNaoEncontradoException")
    void handleDomainException_recursoNaoEncontrado_deveRetornar404() {
        RecursoNaoEncontradoException ex = RecursoNaoEncontradoException.builder()
                .mensagem("Recurso não encontrado")
                .detalhes("Detalhe do erro")
                .build();

        ResponseEntity<ProblemDetail> response = handler.handleDomainException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recurso não encontrado", response.getBody().getDetail());
        assertEquals("RECURSO_NAO_ENCONTRADO", response.getBody().getTitle());
        var props = response.getBody().getProperties();
        assertNotNull(props);
        assertEquals("Detalhe do erro", props.get("detalhes"));
    }

    @Test
    @DisplayName("Deve retornar 409 para RecursoJaExisteException")
    void handleDomainException_recursoJaExiste_deveRetornar409() {
        RecursoJaExisteException ex = RecursoJaExisteException.builder()
                .mensagem("Recurso já existe")
                .detalhes("")
                .build();

        ResponseEntity<ProblemDetail> response = handler.handleDomainException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RECURSO_JA_EXISTE", response.getBody().getTitle());
    }

    @Test
    @DisplayName("Deve incluir tipo correto na URI do ProblemDetail")
    void handleDomainException_deveIncluirTipoNaUri() {
        RecursoNaoEncontradoException ex = RecursoNaoEncontradoException.builder()
                .mensagem("msg")
                .detalhes("")
                .build();

        ResponseEntity<ProblemDetail> response = handler.handleDomainException(ex, request);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().getType().toString().contains("recurso_nao_encontrado"));
    }

    @Test
    @DisplayName("Deve incluir timestamp no ProblemDetail de DomainException")
    void handleDomainException_deveIncluirTimestamp() {
        RecursoNaoEncontradoException ex = RecursoNaoEncontradoException.builder()
                .mensagem("msg")
                .detalhes("")
                .build();

        ResponseEntity<ProblemDetail> response = handler.handleDomainException(ex, request);

        assertNotNull(response.getBody());
        var props = response.getBody().getProperties();
        assertNotNull(props);
        assertNotNull(props.get("timestamp"));
    }

    // ─── Exception genérica ─────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar 500 para Exception genérica")
    void handleGeneric_deveRetornar500() {
        Exception ex = new RuntimeException("Erro inesperado");

        ResponseEntity<ProblemDetail> response = handler.handleGeneric(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro interno", response.getBody().getTitle());
        assertEquals("Ocorreu um erro inesperado", response.getBody().getDetail());
    }

    @Test
    @DisplayName("Deve incluir timestamp na Exception genérica")
    void handleGeneric_deveIncluirTimestamp() {
        ResponseEntity<ProblemDetail> response = handler.handleGeneric(new RuntimeException("erro"), request);

        assertNotNull(response.getBody());
        var props = response.getBody().getProperties();
        assertNotNull(props);
        assertNotNull(props.get("timestamp"));
    }

    @Test
    @DisplayName("Deve incluir URI correta para erros genéricos")
    void handleGeneric_deveIncluirUriCorreta() {
        ResponseEntity<ProblemDetail> response = handler.handleGeneric(new RuntimeException("erro"), request);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().getType().toString().contains("interno"));
    }

    // ─── AccessDeniedException (403) ────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar 403 para AccessDeniedException")
    void handleAccessDenied_deveRetornar403() throws AccessDeniedException {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");

        ResponseEntity<ProblemDetail> response = handler.handleAccessDenied(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Acesso negado", response.getBody().getTitle());
        assertEquals(
                "Você não tem permissão para acessar este recurso",
                response.getBody().getDetail());
    }

    @Test
    @DisplayName("Deve incluir URI correta para acesso negado")
    void handleAccessDenied_deveIncluirUriCorreta() throws AccessDeniedException {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");

        ResponseEntity<ProblemDetail> response = handler.handleAccessDenied(ex, request);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().getType().toString().contains("acesso-negado"));
    }

    @Test
    @DisplayName("Deve incluir timestamp e path no ProblemDetail de acesso negado")
    void handleAccessDenied_deveIncluirTimestampEPath() throws AccessDeniedException {
        when(request.getRequestURI()).thenReturn("/api/erros-log");
        AccessDeniedException ex = new AccessDeniedException("Access Denied");

        ResponseEntity<ProblemDetail> response = handler.handleAccessDenied(ex, request);

        assertNotNull(response.getBody());
        var props = response.getBody().getProperties();
        assertNotNull(props);
        assertNotNull(props.get("timestamp"));
        assertEquals("/api/erros-log", props.get("path"));
    }

    @Test
    @DisplayName("Deve retornar 403 para AuthorizationDeniedException (subclasse de AccessDeniedException)")
    void handleAccessDenied_authorizationDeniedException_deveRetornar403() throws AccessDeniedException {
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access Denied", () -> false);

        ResponseEntity<ProblemDetail> response = handler.handleAccessDenied(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Acesso negado", response.getBody().getTitle());
    }

    @Test
    @DisplayName("Não deve chamar registrar() no ErroLogUseCase para acesso negado")
    void handleAccessDenied_naoDeveLogar() throws AccessDeniedException {
        ErroLogUseCase erroLogMock = mock(ErroLogUseCase.class);
        GlobalExceptionHandler handlerComMock = new GlobalExceptionHandler(erroLogMock);
        // SecurityContext já tem usuário autenticado (configurado no @BeforeEach)
        handlerComMock.handleAccessDenied(new AccessDeniedException("denied"), request);

        verify(erroLogMock, never()).registrar(any());
    }

    // ─── BadCredentialsException / UsernameNotFoundException (401) ──────────

    @Test
    @DisplayName("Deve retornar 401 para BadCredentialsException")
    void handleBadCredentials_deveRetornar401() {
        BadCredentialsException ex = new BadCredentialsException("Credenciais inválidas");

        ResponseEntity<ProblemDetail> response = handler.handleBadCredentials(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Credenciais inválidas", response.getBody().getTitle());
        assertEquals("E-mail ou senha incorretos", response.getBody().getDetail());
    }

    @Test
    @DisplayName("Deve retornar 401 para UsernameNotFoundException")
    void handleUsernameNotFound_deveRetornar401() {
        UsernameNotFoundException ex = new UsernameNotFoundException("Usuário não encontrado: test@test.com");

        ResponseEntity<ProblemDetail> response = handler.handleUsernameNotFound(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Credenciais inválidas", response.getBody().getTitle());
        assertEquals("E-mail ou senha incorretos", response.getBody().getDetail());
    }

    @Test
    @DisplayName("Deve usar mensagem genérica para não vazar se o e-mail existe (anti user-enumeration)")
    void handleUsernameNotFound_deveMensagemGenerica() {
        UsernameNotFoundException ex = new UsernameNotFoundException("Usuário não encontrado: rodrigo@email.com");

        ResponseEntity<ProblemDetail> response = handler.handleUsernameNotFound(ex, request);

        assertNotNull(response.getBody());
        // A detail nunca deve revelar que o e-mail não existe
        assertFalse(response.getBody().getDetail().toLowerCase().contains("não encontrado"));
        assertFalse(response.getBody().getDetail().toLowerCase().contains("usuário"));
    }

    @Test
    @DisplayName("Deve incluir timestamp e path no ProblemDetail de credenciais inválidas")
    void handleBadCredentials_deveIncluirTimestampEPath() {
        when(request.getRequestURI()).thenReturn("/pessoas/login");
        BadCredentialsException ex = new BadCredentialsException("bad creds");

        ResponseEntity<ProblemDetail> response = handler.handleBadCredentials(ex, request);

        assertNotNull(response.getBody());
        var props = response.getBody().getProperties();
        assertNotNull(props);
        assertNotNull(props.get("timestamp"));
        assertEquals("/pessoas/login", props.get("path"));
    }

    @Test
    @DisplayName("Deve incluir URI correta para credenciais inválidas")
    void handleBadCredentials_deveIncluirUriCorreta() {
        BadCredentialsException ex = new BadCredentialsException("bad creds");

        ResponseEntity<ProblemDetail> response = handler.handleBadCredentials(ex, request);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().getType().toString().contains("credenciais-invalidas"));
    }

    @Test
    @DisplayName("Não deve chamar registrar() no ErroLogUseCase para BadCredentialsException")
    void handleBadCredentials_naoDeveLogar() {
        ErroLogUseCase erroLogMock = mock(ErroLogUseCase.class);
        GlobalExceptionHandler handlerComMock = new GlobalExceptionHandler(erroLogMock);

        handlerComMock.handleBadCredentials(new BadCredentialsException("bad creds"), request);

        verify(erroLogMock, never()).registrar(any());
    }

    @Test
    @DisplayName("Não deve chamar registrar() no ErroLogUseCase para UsernameNotFoundException")
    void handleUsernameNotFound_naoDeveLogar() {
        ErroLogUseCase erroLogMock = mock(ErroLogUseCase.class);
        GlobalExceptionHandler handlerComMock = new GlobalExceptionHandler(erroLogMock);

        handlerComMock.handleUsernameNotFound(new UsernameNotFoundException("not found"), request);

        verify(erroLogMock, never()).registrar(any());
    }

    // ─── InternalAuthenticationServiceException (401) ───────────────────────

    @Test
    @DisplayName("Deve retornar 401 para InternalAuthenticationServiceException")
    void handleInternalAuthService_deveRetornar401() {
        InternalAuthenticationServiceException ex = new InternalAuthenticationServiceException(
                "Usuário não encontrado",
                new UsernameNotFoundException("Usuário não encontrado: test@test.com"));

        ResponseEntity<ProblemDetail> response = handler.handleInternalAuthService(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Credenciais inválidas", response.getBody().getTitle());
        assertEquals("E-mail ou senha incorretos", response.getBody().getDetail());
    }

    @Test
    @DisplayName("InternalAuthenticationServiceException deve usar mensagem genérica (anti user-enumeration)")
    void handleInternalAuthService_deveMensagemGenerica() {
        InternalAuthenticationServiceException ex = new InternalAuthenticationServiceException(
                "Usuário não encontrado: rodrigo@email.com",
                new UsernameNotFoundException("Usuário não encontrado: rodrigo@email.com"));

        ResponseEntity<ProblemDetail> response = handler.handleInternalAuthService(ex, request);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().getDetail().toLowerCase().contains("não encontrado"));
        assertFalse(response.getBody().getDetail().toLowerCase().contains("usuário"));
    }

    @Test
    @DisplayName("Não deve chamar registrar() no ErroLogUseCase para InternalAuthenticationServiceException")
    void handleInternalAuthService_naoDeveLogar() {
        ErroLogUseCase erroLogMock = mock(ErroLogUseCase.class);
        GlobalExceptionHandler handlerComMock = new GlobalExceptionHandler(erroLogMock);

        handlerComMock.handleInternalAuthService(
                new InternalAuthenticationServiceException("falha", new RuntimeException("causa")), request);

        verify(erroLogMock, never()).registrar(any());
    }
}
