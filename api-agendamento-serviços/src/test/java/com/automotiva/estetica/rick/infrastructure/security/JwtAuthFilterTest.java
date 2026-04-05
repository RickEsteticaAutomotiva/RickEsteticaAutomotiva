package com.automotiva.estetica.rick.infrastructure.security;

import com.automotiva.estetica.rick.application.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de JwtAuthFilter")
class JwtAuthFilterTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve retornar 401 quando token estiver expirado")
    void deveRetornar401QuandoTokenExpirado() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer expired-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.obterUsernameDoToken("expired-token"))
                .thenThrow(new ExpiredJwtException(null, null, "Token expirado"));

        jwtAuthFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals("Token expirado", response.getErrorMessage());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve retornar 401 quando token estiver invalido")
    void deveRetornar401QuandoTokenInvalido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.obterUsernameDoToken("invalid-token")).thenThrow(new JwtException("Token invalido"));

        jwtAuthFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals("Token invalido", response.getErrorMessage());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve retornar 401 quando usuario do token nao existir")
    void deveRetornar401QuandoUsuarioDoTokenNaoExiste() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.obterUsernameDoToken("valid-token")).thenReturn("nao.existe@email.com");
        when(userDetailsService.loadUserByUsername("nao.existe@email.com")).thenThrow(
                new org.springframework.security.core.userdetails.UsernameNotFoundException("Nao encontrado"));

        jwtAuthFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals("Token invalido", response.getErrorMessage());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve seguir cadeia quando token valido")
    void deveSeguirCadeiaQuandoTokenValido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails user = User.withUsername("usuario@email.com").password("x").authorities("ROLE_CLIENTE").build();

        when(jwtService.obterUsernameDoToken("valid-token")).thenReturn("usuario@email.com");
        when(userDetailsService.loadUserByUsername("usuario@email.com")).thenReturn(user);
        when(jwtService.tokenValido("valid-token", user)).thenReturn(true);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve seguir cadeia quando não houver header Authorization")
    void deveSeguirCadeiaQuandoSemAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve seguir cadeia sem autenticar quando tokenValido retornar false")
    void deveSeguirCadeiaQuandoTokenInvalidoNaValidacao() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails user = User.withUsername("usuario@email.com").password("x").authorities("ROLE_CLIENTE").build();

        when(jwtService.obterUsernameDoToken("valid-token")).thenReturn("usuario@email.com");
        when(userDetailsService.loadUserByUsername("usuario@email.com")).thenReturn(user);
        when(jwtService.tokenValido("valid-token", user)).thenReturn(false);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve seguir cadeia quando header Authorization nao usa Bearer")
    void deveSeguirCadeiaQuandoHeaderAuthorizationNaoBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve retornar 401 quando token expira durante validacao")
    void deveRetornar401QuandoTokenExpiraDuranteValidacao() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails user = User.withUsername("usuario@email.com").password("x").authorities("ROLE_CLIENTE").build();
        when(jwtService.obterUsernameDoToken("valid-token")).thenReturn("usuario@email.com");
        when(userDetailsService.loadUserByUsername("usuario@email.com")).thenReturn(user);
        when(jwtService.tokenValido("valid-token", user)).thenThrow(new ExpiredJwtException(null, null, "expirado"));

        jwtAuthFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals("Token expirado", response.getErrorMessage());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve retornar 401 quando token invalido durante validacao")
    void deveRetornar401QuandoTokenInvalidoDuranteValidacao() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails user = User.withUsername("usuario@email.com").password("x").authorities("ROLE_CLIENTE").build();
        when(jwtService.obterUsernameDoToken("valid-token")).thenReturn("usuario@email.com");
        when(userDetailsService.loadUserByUsername("usuario@email.com")).thenReturn(user);
        when(jwtService.tokenValido("valid-token", user)).thenThrow(new JwtException("token ruim"));

        jwtAuthFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals("Token invalido", response.getErrorMessage());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve seguir cadeia quando token nao retornar username")
    void deveSeguirCadeiaQuandoTokenSemUsername() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-sem-username");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.obterUsernameDoToken("token-sem-username")).thenReturn(null);

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(org.mockito.ArgumentMatchers.anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
