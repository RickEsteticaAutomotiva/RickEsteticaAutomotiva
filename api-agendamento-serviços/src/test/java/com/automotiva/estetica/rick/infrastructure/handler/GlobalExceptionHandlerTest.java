package com.automotiva.estetica.rick.infrastructure.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.port.in.ErroLogUseCase;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        ErroLogUseCase erroLogUseCase = mock(ErroLogUseCase.class);
        // registrar() é @Async void — o mock simplesmente não faz nada, o que é correto para testes unitários
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
    void handleAccessDenied_deveRetornar403() {
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
    void handleAccessDenied_deveIncluirUriCorreta() {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");

        ResponseEntity<ProblemDetail> response = handler.handleAccessDenied(ex, request);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().getType().toString().contains("acesso-negado"));
    }

    @Test
    @DisplayName("Deve incluir timestamp e path no ProblemDetail de acesso negado")
    void handleAccessDenied_deveIncluirTimestampEPath() {
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
    void handleAccessDenied_authorizationDeniedException_deveRetornar403() {
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access Denied", () -> false);

        ResponseEntity<ProblemDetail> response = handler.handleAccessDenied(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Acesso negado", response.getBody().getTitle());
    }

    @Test
    @DisplayName("Não deve chamar registrar() no ErroLogUseCase para acesso negado")
    void handleAccessDenied_naoDeveLogar() {
        ErroLogUseCase erroLogMock = mock(ErroLogUseCase.class);
        GlobalExceptionHandler handlerComMock = new GlobalExceptionHandler(erroLogMock);

        handlerComMock.handleAccessDenied(new AccessDeniedException("denied"), request);

        verify(erroLogMock, never()).registrar(any());
    }
}
