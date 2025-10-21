package com.automotiva.estetica.rick.api_agendamento_servicos.config;

import com.automotiva.estetica.rick.api_agendamento_servicos.service.PessoaService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class AutenticacaoFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutenticacaoFilter.class);

    private final PessoaService pessoaService;
    private final GerenciadorTokenJwt gerenciadorTokenJwt;

    private static final List<String> PATHS_PUBLICOS = List.of(
            "/swagger-ui/",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars/",
            "/h2-console/",
            "/error/",
            "/pessoas/login",
            "/users/login",
            "/public/"
    );

    public AutenticacaoFilter(PessoaService pessoaService, GerenciadorTokenJwt gerenciadorTokenJwt) {
        this.pessoaService = pessoaService;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // Ignorar paths públicos
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                username = gerenciadorTokenJwt.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Não foi possível obter o JWT Token: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                LOGGER.warn("JWT Token expirado para o usuário: {}", e.getClaims().getSubject());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = pessoaService.loadUserByUsername(username);

            if (gerenciadorTokenJwt.isTokenValido(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return PATHS_PUBLICOS.stream().anyMatch(path::startsWith);
    }
}
