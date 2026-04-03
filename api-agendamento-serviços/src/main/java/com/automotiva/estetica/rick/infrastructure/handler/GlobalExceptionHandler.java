package com.automotiva.estetica.rick.infrastructure.handler;

import com.automotiva.estetica.rick.application.port.in.ErroLogUseCase;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.exception.DomainException;
import com.automotiva.estetica.rick.infrastructure.security.SensitiveDataRedactor;
import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * Handler global de exceções com persistência de log.
 *
 * <p>
 * Todo erro capturado aqui é salvo de forma assíncrona na tabela erro_log,
 * contendo todos os insumos necessários para reprodução posterior.
 *
 * <p>
 * Camada: infrastructure/handler.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String[] HEADERS_SENSIVEIS = {"authorization", "cookie", "set-cookie"};
    private static final int MAX_PAYLOAD_SIZE = 10_000;

    private final ErroLogUseCase erroLogUseCase;

    public GlobalExceptionHandler(ErroLogUseCase erroLogUseCase) {
        this.erroLogUseCase = erroLogUseCase;
    }

    // -------------------------------------------------------------------------
    // Handlers
    // -------------------------------------------------------------------------

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(DomainException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMensagem());
        problem.setType(URI.create("https://api.rickestetica.com.br/errors/" + ex.getTipo().toLowerCase()));
        problem.setTitle(ex.getTipo());
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("detalhes", ex.getDetalhes());

        registrarLog(ex, ex.getStatus().value(), request);
        return ResponseEntity.status(ex.getStatus()).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("https://api.rickestetica.com.br/errors/validacao"));
        problem.setTitle("Erro de validação");
        problem.setDetail("Um ou mais campos possuem valores inválidos");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("campos",
                ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).toList());

        registrarLog(ex, HttpStatus.BAD_REQUEST.value(), request);
        return ResponseEntity.badRequest().body(problem);
    }

    /**
     * Trata campos de ordenação (sort) inválidos enviados na query string.
     *
     * <p>
     * O Spring Data lança {@link PropertyReferenceException} quando o parâmetro
     * {@code sort} contém um nome de propriedade que não existe na entidade (ex:
     * {@code sort=["string"]}). Isso é erro do cliente, portanto retorna 400 Bad
     * Request — nunca 500.
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ProblemDetail> handlePropertyReference(PropertyReferenceException ex,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("https://api.rickestetica.com.br/errors/parametro-invalido"));
        problem.setTitle("Parâmetro de ordenação inválido");
        problem.setDetail("O campo informado no parâmetro 'sort' não existe: '" + ex.getPropertyName() + "'");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("sort", request.getParameter("sort"));
        return ResponseEntity.badRequest().body(problem);
    }

    /**
     * Trata negações de acesso lançadas pelo Spring Security
     * (@PreAuthorize, @Secured, etc.).
     *
     * <p>
     * Se o usuário não está autenticado (anônimo), relança a exceção para que o
     * {@code
     * authenticationEntryPoint} do Spring Security devolva 401 UNAUTHORIZED. Se
     * está autenticado mas sem permissão, retorna 403 FORBIDDEN com ProblemDetail.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request)
            throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAnonymous = auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal());
        if (isAnonymous) {
            // Não autenticado — deixa o authenticationEntryPoint devolver 401
            throw ex;
        }
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setType(URI.create("https://api.rickestetica.com.br/errors/acesso-negado"));
        problem.setTitle("Acesso negado");
        problem.setDetail("Você não tem permissão para acessar este recurso");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("motivo", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    /**
     * Trata falhas de autenticação por credenciais inválidas (e-mail ou senha
     * errados).
     *
     * <p>
     * Retorna 401 UNAUTHORIZED com mensagem genérica para evitar <em>user
     * enumeration attack</em>. Não persiste no erro_log pois é um evento de
     * segurança esperado, não um erro de servidor.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.debug("Tentativa de login com credenciais inválidas em [{}]: {}", request.getRequestURI(), ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setType(URI.create("https://api.rickestetica.com.br/errors/credenciais-invalidas"));
        problem.setTitle("Credenciais inválidas");
        problem.setDetail("E-mail ou senha incorretos");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    /**
     * Trata o wrapper lançado pelo Spring Security quando o UserDetailsService
     * lança qualquer exceção durante a autenticação (ex:
     * UsernameNotFoundException).
     *
     * <p>
     * O Spring Security captura exceções não-checadas lançadas dentro do
     * AuthenticationProvider e as re-lança envoltas em
     * InternalAuthenticationServiceException — por isso a UsernameNotFoundException
     * nunca chega diretamente ao handler. Este método desempacota e delega para
     * handleBadCredentials para retornar 401 com mensagem genérica.
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ProblemDetail> handleInternalAuthService(InternalAuthenticationServiceException ex,
            HttpServletRequest request) {
        log.debug("Falha interna de autenticação em [{}] — causa: {}", request.getRequestURI(),
                ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
        return handleBadCredentials(new BadCredentialsException("E-mail ou senha incorretos", ex), request);
    }

    /**
     * Trata o caso em que o e-mail informado no login não existe na base.
     *
     * <p>
     * O {@link org.springframework.security.authentication.AuthenticationProvider}
     * customizado não herda de {@code DaoAuthenticationProvider}, portanto a
     * {@code UsernameNotFoundException} não é convertida automaticamente em
     * {@code BadCredentialsException}. Este handler garante que qualquer um dos
     * dois caminhos retorne sempre 401 com a mesma mensagem genérica.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUsernameNotFound(UsernameNotFoundException ex,
            HttpServletRequest request) {
        log.debug("Tentativa de login com e-mail inexistente em [{}]: {}", request.getRequestURI(), ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setType(URI.create("https://api.rickestetica.com.br/errors/credenciais-invalidas"));
        problem.setTitle("Credenciais inválidas");
        problem.setDetail("E-mail ou senha incorretos");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setType(URI.create("https://api.rickestetica.com.br/errors/interno"));
        problem.setTitle("Erro interno");
        problem.setDetail("Ocorreu um erro inesperado");
        problem.setProperty("timestamp", Instant.now());

        registrarLog(ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), request);
        return ResponseEntity.internalServerError().body(problem);
    }

    // -------------------------------------------------------------------------
    // Montagem e persistência do log
    // -------------------------------------------------------------------------

    private void registrarLog(Exception ex, int statusHttp, HttpServletRequest request) {
        try {
            // Stack trace apenas para erros 5xx (servidor); 4xx são esperados
            String stackTrace = (statusHttp >= 500) ? extrairStackTrace(ex) : null;

            ErroLog erroLog = ErroLog.builder().timestamp(LocalDateTime.now()).tipoExcecao(ex.getClass().getName())
                    .mensagem(ex.getMessage()).stackTrace(stackTrace) // null para 4xx
                    .endpoint(request.getRequestURI()).metodoHttp(request.getMethod())
                    .payloadRequisicao(extrairPayload(request)) // mascarado via SensitiveDataRedactor
                    .queryParams(SensitiveDataRedactor.redactPayload(request.getQueryString()))
                    .headersRequisicao(extrairHeaders(request)) // ja filtra sensiveis
                    .usuarioEmail(obterUsuarioAutenticado()).statusHttp(statusHttp).ambiente(obterAmbiente())
                    .ipCliente(obterIpCliente(request)).userAgent(request.getHeader("User-Agent")).build();

            erroLogUseCase.registrar(erroLog);
        } catch (Exception logEx) {
            // Falha ao logar NUNCA pode interferir na resposta ao cliente
            log.warn("Falha ao persistir log de erro. Causa: {}", logEx.getMessage(), logEx);
        }
    }

    private String extrairStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String extrairPayload(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, MAX_PAYLOAD_SIZE);
                String payload = new String(buf, 0, length, StandardCharsets.UTF_8);
                // Mascara campos sensíveis (senha, token, cpf, etc.)
                return SensitiveDataRedactor.redactPayload(payload);
            }
        }
        return null;
    }

    private String extrairHeaders(HttpServletRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement().toLowerCase();
                if (!isSensivel(name)) {
                    // Se não estiver na lista preta, ainda mascara se for reconhecida como sensível
                    String value = request.getHeader(name);
                    headers.put(name, SensitiveDataRedactor.redactHeader(name, value));
                }
            }
        }
        return headers.toString();
    }

    private boolean isSensivel(String headerName) {
        for (String sensivel : HEADERS_SENSIVEIS) {
            if (sensivel.equalsIgnoreCase(headerName)) {
                return true;
            }
        }
        return false;
    }

    private String obterUsuarioAutenticado() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                return auth.getName();
            }
        } catch (Exception ex) {
            log.debug("Não foi possível obter o usuário autenticado do SecurityContext", ex);
        }
        return "anonimo";
    }

    private String obterAmbiente() {
        String profile = System.getProperty("spring.profiles.active");
        return profile != null ? profile : "desconhecido";
    }

    private String obterIpCliente(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
