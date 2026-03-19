package com.automotiva.estetica.rick.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Filtro que envolve o HttpServletRequest com ContentCachingRequestWrapper.
 *
 * <p>Responsabilidades:
 * <ul>
 * <li>Gera e armazena um `requestId` no MDC para rastreabilidade em logs.
 * <li>Envolve request com caching para que GlobalExceptionHandler leia o body
 * após consumo pelo Spring MVC.
 * </ul>
 *
 * <p>@Order(1) garante execução antes de todos os filtros de segurança.
 */
@Component
@Order(1)
public class RequestCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Gera e armazena requestId único no MDC
        RequestIdHolder.generateAndStoreRequestId();

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            wrappedResponse.copyBodyToResponse();
            // Limpa MDC ao final da requisição
            RequestIdHolder.clear();
        }
    }
}
