package com.automotiva.estetica.rick.infrastructure.security;

import com.automotiva.estetica.rick.application.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class);
    private static final String TOKEN_EXPIRADO = "Token expirado";
    private static final String TOKEN_INVALIDO = "Token invalido";

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    // @Lazy no UserDetailsService para quebrar o ciclo:
    // PessoaApplicationService -> SecurityConfig -> JwtAuthFilter ->
    // PessoaApplicationService
    public JwtAuthFilter(@Lazy UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                username = jwtService.obterUsernameDoToken(jwtToken);
            } catch (ExpiredJwtException e) {
                LOGGER.info("Token expirado: {}", e.getMessage());
                sendUnauthorized(response, TOKEN_EXPIRADO);
                return;
            } catch (JwtException | IllegalArgumentException e) {
                LOGGER.info("Token invalido: {}", e.getMessage());
                sendUnauthorized(response, TOKEN_INVALIDO);
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.tokenValido(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (ExpiredJwtException e) {
                LOGGER.info("Token expirado durante validacao: {}", e.getMessage());
                sendUnauthorized(response, TOKEN_EXPIRADO);
                return;
            } catch (UsernameNotFoundException | JwtException | IllegalArgumentException e) {
                LOGGER.info("Token invalido durante validacao: {}", e.getMessage());
                sendUnauthorized(response, TOKEN_INVALIDO);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }
}
