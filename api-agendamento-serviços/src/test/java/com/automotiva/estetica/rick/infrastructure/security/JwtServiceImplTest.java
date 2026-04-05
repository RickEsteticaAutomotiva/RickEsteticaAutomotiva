package com.automotiva.estetica.rick.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("Testes de JwtServiceImpl")
class JwtServiceImplTest {

    private static final String SECRET = "0123456789012345678901234567890123456789012345678901234567890123";

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtTokenValidity", 3600L);
    }

    @Test
    @DisplayName("deve gerar token com username e roles")
    void gerarToken_deveConterSubjectERoles() {
        var authentication = new UsernamePasswordAuthenticationToken("user@rick.com", "x",
                List.of(() -> "ROLE_CLIENTE", () -> "ROLE_GERENTE"));

        String token = jwtService.gerarToken(authentication);

        assertEquals("user@rick.com", jwtService.obterUsernameDoToken(token));
        assertEquals(List.of("ROLE_CLIENTE", "ROLE_GERENTE"), jwtService.getRolesFromToken(token));
    }

    @Test
    @DisplayName("tokenValido deve retornar true quando usuario bater")
    void tokenValido_deveRetornarTrueQuandoUsuarioCorresponder() {
        var authentication = new UsernamePasswordAuthenticationToken("ana@rick.com", "x", List.of(() -> "ROLE_CLIENTE"));
        String token = jwtService.gerarToken(authentication);
        UserDetails userDetails = User.withUsername("ana@rick.com").password("x").authorities("ROLE_CLIENTE").build();

        assertTrue(jwtService.tokenValido(token, userDetails));
    }

    @Test
    @DisplayName("tokenValido deve retornar false quando usuario nao bater")
    void tokenValido_deveRetornarFalseQuandoUsuarioNaoCorresponder() {
        var authentication = new UsernamePasswordAuthenticationToken("ana@rick.com", "x", List.of(() -> "ROLE_CLIENTE"));
        String token = jwtService.gerarToken(authentication);
        UserDetails outroUsuario = User.withUsername("outro@rick.com").password("x").authorities("ROLE_CLIENTE").build();

        assertFalse(jwtService.tokenValido(token, outroUsuario));
    }

    @Test
    @DisplayName("getRolesFromToken deve retornar lista vazia quando claim roles nao existir")
    void getRolesFromToken_deveRetornarVazioSemRoles() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String tokenSemRoles = Jwts.builder().subject("sem-roles@rick.com").issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000)).signWith(key).compact();

        List<String> roles = jwtService.getRolesFromToken(tokenSemRoles);

        assertTrue(roles.isEmpty());
    }

    @Test
    @DisplayName("obterUsernameDoToken deve lancar excecao para token invalido")
    void obterUsernameDoToken_deveLancarParaTokenInvalido() {
        assertThrows(Exception.class, () -> jwtService.obterUsernameDoToken("token-invalido"));
    }
}


