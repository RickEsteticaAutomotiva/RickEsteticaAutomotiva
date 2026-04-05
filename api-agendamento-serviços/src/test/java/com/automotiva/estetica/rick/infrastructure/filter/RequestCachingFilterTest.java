package com.automotiva.estetica.rick.infrastructure.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@DisplayName("Testes de RequestCachingFilter")
class RequestCachingFilterTest {

    private final RequestCachingFilter requestCachingFilter = new RequestCachingFilter();

    @Test
    @DisplayName("deve envolver request/response e limpar requestId ao final")
    void doFilterInternal_deveEnvolverRequestResponseELimparRequestId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/teste");
        request.setContent("payload".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainExecutada = new AtomicBoolean(false);

        FilterChain filterChain = (req, res) -> {
            chainExecutada.set(true);
            assertInstanceOf(ContentCachingRequestWrapper.class, req);
            assertInstanceOf(ContentCachingResponseWrapper.class, res);
            assertNotNull(RequestIdHolder.getRequestId());
            ((ContentCachingResponseWrapper) res).getOutputStream().write("ok".getBytes(StandardCharsets.UTF_8));
        };

        requestCachingFilter.doFilter(request, response, filterChain);

        assertTrue(chainExecutada.get());
        assertEquals("ok", response.getContentAsString());
        assertNull(RequestIdHolder.getRequestId());
    }

    @Test
    @DisplayName("deve limpar requestId mesmo quando chain lancar excecao")
    void doFilterInternal_deveLimparRequestIdQuandoChainFalhar() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/teste");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain filterChain = (req, res) -> {
            throw new ServletException("falha no filtro");
        };

        assertThrows(ServletException.class, () -> requestCachingFilter.doFilter(request, response, filterChain));
        assertNull(RequestIdHolder.getRequestId());
    }
}


