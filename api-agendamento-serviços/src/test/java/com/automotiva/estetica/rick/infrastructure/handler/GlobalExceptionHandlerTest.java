package com.automotiva.estetica.rick.infrastructure.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.automotiva.estetica.rick.application.service.ErroLogApplicationService;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.web.util.ContentCachingRequestWrapper;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;
    private ErroLogApplicationService erroLogUseCase;

    @BeforeEach
    void setUp() {
        erroLogUseCase = mock(ErroLogApplicationService.class);
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

        // Popula SecurityContext com usuário autenticado (simula ROLE_ADMIN
        // autenticado)
        // necessário para que handleAccessDenied retorne 403 (não 401 para anônimos)
        var auth = new UsernamePasswordAuthenticationToken("admin@test.com", null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        System.clearProperty("spring.profiles.active");
    }

    @AfterAll
    static void clearSystemPropertyAtEnd() {
        System.clearProperty("spring.profiles.active");
    }

    private static class DummyValidationController {
        @SuppressWarnings("unused")
        void endpoint(@Valid DummyPayload payload) {
        }
    }

    private static class DummyPayload {
        @SuppressWarnings("unused")
        @NotBlank
        private String nome;
    }

    // ─── DomainException ────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar 404 para RecursoNaoEncontradoException")
    void handleDomainException_recursoNaoEncontrado_deveRetornar404() {
        RecursoNaoEncontradoException ex = RecursoNaoEncontradoException.builder().mensagem("Recurso não encontrado")
                .detalhes("Detalhe do erro").build();

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
        RecursoJaExisteException ex = RecursoJaExisteException.builder().mensagem("Recurso já existe").detalhes("")
                .build();

        ResponseEntity<ProblemDetail> response = handler.handleDomainException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RECURSO_JA_EXISTE", response.getBody().getTitle());
    }

    @Test
    @DisplayName("Deve incluir tipo correto na URI do ProblemDetail")
    void handleDomainException_deveIncluirTipoNaUri() {
        RecursoNaoEncontradoException ex = RecursoNaoEncontradoException.builder().mensagem("msg").detalhes("").build();

        ResponseEntity<ProblemDetail> response = handler.handleDomainException(ex, request);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().getType().toString().contains("recurso_nao_encontrado"));
    }

    @Test
    @DisplayName("Deve incluir timestamp no ProblemDetail de DomainException")
    void handleDomainException_deveIncluirTimestamp() {
        RecursoNaoEncontradoException ex = RecursoNaoEncontradoException.builder().mensagem("msg").detalhes("").build();

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

    @Test
    @DisplayName("Falha ao persistir erro_log não deve quebrar resposta ao cliente")
    void handleGeneric_quandoFalharPersistenciaDeLog_deveManter500() {
        ErroLogApplicationService erroLogComFalha = mock(ErroLogApplicationService.class);
        doThrow(new RuntimeException("db indisponivel")).when(erroLogComFalha).registrar(any());
        GlobalExceptionHandler handlerComFalha = new GlobalExceptionHandler(erroLogComFalha);

        ResponseEntity<ProblemDetail> response = handlerComFalha.handleGeneric(new RuntimeException("erro"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro interno", response.getBody().getTitle());
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
        assertEquals("Você não tem permissão para acessar este recurso", response.getBody().getDetail());
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
    @DisplayName("Não deve chamar registrar() no ErroLogApplicationService para acesso negado")
    void handleAccessDenied_naoDeveLogar() throws AccessDeniedException {
        ErroLogApplicationService erroLogMock = mock(ErroLogApplicationService.class);
        GlobalExceptionHandler handlerComMock = new GlobalExceptionHandler(erroLogMock);
        // SecurityContext já tem usuário autenticado (configurado no @BeforeEach)
        handlerComMock.handleAccessDenied(new AccessDeniedException("denied"), request);

        verify(erroLogMock, never()).registrar(any());
    }

    @Test
    @DisplayName("Deve relançar AccessDeniedException quando usuário for anônimo")
    void handleAccessDenied_quandoAnonimo_deveRelancarExcecao() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("key", "anonymousUser",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        AccessDeniedException ex = new AccessDeniedException("Access Denied");

        assertThrows(AccessDeniedException.class, () -> handler.handleAccessDenied(ex, request));
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
        assertNotNull(response.getBody().getDetail());
        // A detail nunca deve revelar que o e-mail não existe
        String detail = response.getBody().getDetail().toLowerCase();
        assertFalse(detail.contains("não encontrado"));
        assertFalse(detail.contains("usuário"));
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
    @DisplayName("Não deve chamar registrar() no ErroLogApplicationService para BadCredentialsException")
    void handleBadCredentials_naoDeveLogar() {
        ErroLogApplicationService erroLogMock = mock(ErroLogApplicationService.class);
        GlobalExceptionHandler handlerComMock = new GlobalExceptionHandler(erroLogMock);

        handlerComMock.handleBadCredentials(new BadCredentialsException("bad creds"), request);

        verify(erroLogMock, never()).registrar(any());
    }

    @Test
    @DisplayName("Não deve chamar registrar() no ErroLogApplicationService para UsernameNotFoundException")
    void handleUsernameNotFound_naoDeveLogar() {
        ErroLogApplicationService erroLogMock = mock(ErroLogApplicationService.class);
        GlobalExceptionHandler handlerComMock = new GlobalExceptionHandler(erroLogMock);

        handlerComMock.handleUsernameNotFound(new UsernameNotFoundException("not found"), request);

        verify(erroLogMock, never()).registrar(any());
    }

    // ─── InternalAuthenticationServiceException (401) ───────────────────────

    @Test
    @DisplayName("Deve retornar 401 para InternalAuthenticationServiceException")
    void handleInternalAuthService_deveRetornar401() {
        InternalAuthenticationServiceException ex = new InternalAuthenticationServiceException("Usuário não encontrado",
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
        assertNotNull(response.getBody().getDetail());
        String detail = response.getBody().getDetail().toLowerCase();
        assertFalse(detail.contains("não encontrado"));
        assertFalse(detail.contains("usuário"));
    }

    @Test
    @DisplayName("Não deve chamar registrar() no ErroLogApplicationService para InternalAuthenticationServiceException")
    void handleInternalAuthService_naoDeveLogar() {
        ErroLogApplicationService erroLogMock = mock(ErroLogApplicationService.class);
        GlobalExceptionHandler handlerComMock = new GlobalExceptionHandler(erroLogMock);

        handlerComMock.handleInternalAuthService(
                new InternalAuthenticationServiceException("falha", new RuntimeException("causa")), request);

        verify(erroLogMock, never()).registrar(any());
    }

    @Test
    @DisplayName("Deve retornar 401 em InternalAuthenticationServiceException sem causa")
    void handleInternalAuthService_semCausa_deveRetornar401() {
        InternalAuthenticationServiceException ex = new InternalAuthenticationServiceException("falha sem causa");

        ResponseEntity<ProblemDetail> response = handler.handleInternalAuthService(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("E-mail ou senha incorretos", response.getBody().getDetail());
    }

    @Test
    @DisplayName("Deve retornar 400 e listar campos inválidos")
    void handleValidation_deveRetornar400ComCampos() throws Exception {
        Method method = DummyValidationController.class.getDeclaredMethod("endpoint", DummyPayload.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new DummyPayload(), "payload");
        bindingResult.addError(new FieldError("payload", "nome", "nome é obrigatório"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ProblemDetail> response = handler.handleValidation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getProperties());
        assertEquals("Erro de validação", response.getBody().getTitle());
        assertTrue(((List<?>) response.getBody().getProperties().get("campos")).contains("nome é obrigatório"));
        verify(erroLogUseCase).registrar(any(ErroLog.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando parâmetro sort não existir")
    void handlePropertyReference_deveRetornar400() {
        when(request.getParameter("sort")).thenReturn("campoInvalido,asc");
        PropertyReferenceException ex = new PropertyReferenceException("campoInvalido",
                TypeInformation.of(Object.class), List.of());

        ResponseEntity<ProblemDetail> response = handler.handlePropertyReference(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getDetail());
        assertNotNull(response.getBody().getProperties());
        assertTrue(response.getBody().getDetail().contains("campoInvalido"));
        assertEquals("campoInvalido,asc", response.getBody().getProperties().get("sort"));
    }

    @Test
    @DisplayName("Deve redatar payload/query e preservar dados de auditoria no erro_log")
    void handleGeneric_devePersistirErroLogComDadosRedatados() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "auditoria@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
        System.setProperty("spring.profiles.active", "test");

        MockHttpServletRequest raw = new MockHttpServletRequest("POST", "/api/ordens");
        raw.setContentType("application/json");
        raw.setContent("{\"senha\":\"abc123\",\"nome\":\"Joao\"}".getBytes(StandardCharsets.UTF_8));
        raw.setQueryString("token=abc&page=1");
        raw.addHeader("Authorization", "Bearer abc");
        raw.addHeader("token", "abc");
        raw.addHeader("User-Agent", "JUnit");
        raw.addHeader("X-Forwarded-For", "10.10.10.10, 192.168.0.1");
        raw.setRemoteAddr("127.0.0.1");

        ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(raw);
        wrapper.getInputStream().readAllBytes();

        handler.handleGeneric(new RuntimeException("falha"), wrapper);

        ArgumentCaptor<ErroLog> captor = ArgumentCaptor.forClass(ErroLog.class);
        verify(erroLogUseCase).registrar(captor.capture());
        ErroLog logCapturado = captor.getValue();

        assertNotNull(logCapturado.getPayloadRequisicao());
        assertTrue(logCapturado.getPayloadRequisicao().contains("***REDACTED***"));
        assertFalse(logCapturado.getPayloadRequisicao().contains("abc123"));
        assertNotNull(logCapturado.getQueryParams());
        assertTrue(logCapturado.getQueryParams().contains("***REDACTED***"));
        assertFalse(logCapturado.getHeadersRequisicao().contains("authorization"));
        assertTrue(logCapturado.getHeadersRequisicao().contains("token=***REDACTED***"));
        assertEquals("10.10.10.10", logCapturado.getIpCliente());
        assertEquals("auditoria@test.com", logCapturado.getUsuarioEmail());
        assertEquals("test", logCapturado.getAmbiente());
    }

    @Test
    @DisplayName("Deve persistir usuário anonimo quando SecurityContext estiver vazio")
    void handleDomainException_quandoSemAutenticacao_devePersistirUsuarioAnonimo() {
        SecurityContextHolder.clearContext();
        RecursoNaoEncontradoException ex = RecursoNaoEncontradoException.builder().mensagem("nao encontrado")
                .detalhes("detalhe").build();

        handler.handleDomainException(ex, request);

        ArgumentCaptor<ErroLog> captor = ArgumentCaptor.forClass(ErroLog.class);
        verify(erroLogUseCase).registrar(captor.capture());
        assertEquals("anonimo", captor.getValue().getUsuarioEmail());
    }

    @Test
    @DisplayName("Deve relançar AccessDeniedException quando auth estiver nulo")
    void handleAccessDenied_quandoAuthNulo_deveRelancarExcecao() {
        SecurityContextHolder.clearContext();

        AccessDeniedException ex = new AccessDeniedException("Access Denied");

        assertThrows(AccessDeniedException.class, () -> handler.handleAccessDenied(ex, request));
    }

    @Test
    @DisplayName("Helper extrairPayload deve retornar nulo quando request nao for wrapper")
    void extrairPayload_quandoNaoForContentCachingWrapper_deveRetornarNulo() {
        Object payload = ReflectionTestUtils.invokeMethod(handler, "extrairPayload", request);

        assertNull(payload);
    }

    @Test
    @DisplayName("Helper extrairHeaders deve retornar mapa vazio quando nao houver header names")
    void extrairHeaders_quandoHeaderNamesNulo_deveRetornarMapaVazio() {
        when(request.getHeaderNames()).thenReturn(null);

        String headers = ReflectionTestUtils.invokeMethod(handler, "extrairHeaders", request);

        assertEquals("{}", headers);
    }

    @Test
    @DisplayName("Helper obterIpCliente deve usar remoteAddr quando X-Forwarded-For estiver em branco")
    void obterIpCliente_quandoForwardedForBlank_deveUsarRemoteAddr() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("   ");
        when(request.getRemoteAddr()).thenReturn("127.0.0.9");

        String ip = ReflectionTestUtils.invokeMethod(handler, "obterIpCliente", request);

        assertEquals("127.0.0.9", ip);
    }

    @Test
    @DisplayName("Helper obterUsuarioAutenticado deve retornar nome do usuario autenticado")
    void obterUsuarioAutenticado_quandoAutenticado_deveRetornarNome() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user@test.com",
                null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        String usuario = ReflectionTestUtils.invokeMethod(handler, "obterUsuarioAutenticado");

        assertEquals("user@test.com", usuario);
    }

    @Test
    @DisplayName("Helper obterUsuarioAutenticado deve retornar anonimo para anonymousUser")
    void obterUsuarioAutenticado_quandoAnonymousUser_deveRetornarAnonimo() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of()));

        String usuario = ReflectionTestUtils.invokeMethod(handler, "obterUsuarioAutenticado");

        assertEquals("anonimo", usuario);
    }

    @Test
    @DisplayName("Helper obterUsuarioAutenticado deve retornar anonimo quando auth nao autenticado")
    void obterUsuarioAutenticado_quandoAuthNaoAutenticado_deveRetornarAnonimo() {
        var auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);
        when(auth.getName()).thenReturn("user@test.com");
        SecurityContextHolder.getContext().setAuthentication(auth);

        String usuario = ReflectionTestUtils.invokeMethod(handler, "obterUsuarioAutenticado");

        assertEquals("anonimo", usuario);
    }

    @Test
    @DisplayName("Helper obterUsuarioAutenticado deve retornar anonimo quando SecurityContext falhar")
    void obterUsuarioAutenticado_quandoSecurityContextFalhar_deveRetornarAnonimo() {
        SecurityContextHolderStrategy original = SecurityContextHolder.getContextHolderStrategy();
        SecurityContextHolder.setContextHolderStrategy(new SecurityContextHolderStrategy() {
            @Override
            public org.springframework.security.core.context.SecurityContext createEmptyContext() {
                throw new RuntimeException("falha");
            }

            @Override
            public org.springframework.security.core.context.SecurityContext getContext() {
                throw new RuntimeException("falha");
            }

            @Override
            public void setContext(org.springframework.security.core.context.SecurityContext context) {
            }

            @Override
            public void clearContext() {
            }
        });

        try {
            String usuario = ReflectionTestUtils.invokeMethod(handler, "obterUsuarioAutenticado");
            assertEquals("anonimo", usuario);
        } finally {
            SecurityContextHolder.setContextHolderStrategy(original);
        }
    }

    @Test
    @DisplayName("Helper obterAmbiente deve retornar desconhecido quando property nao definida")
    void obterAmbiente_quandoSemProperty_deveRetornarDesconhecido() {
        System.clearProperty("spring.profiles.active");

        String ambiente = ReflectionTestUtils.invokeMethod(handler, "obterAmbiente");

        assertEquals("desconhecido", ambiente);
    }
}
